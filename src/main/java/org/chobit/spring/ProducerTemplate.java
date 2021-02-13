package org.chobit.spring;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.chobit.spring.config.ConfigUnit;
import org.chobit.spring.exception.KafkaConfigException;
import org.chobit.spring.exception.KafkaException;
import org.springframework.beans.factory.DisposableBean;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author robin
 */
public class ProducerTemplate<K, V> implements Shutdown, DisposableBean {


    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final Map<String, Producer<K, V>> producers = new HashMap<>(8);

    private final Set<Producer<K, V>> set = new HashSet<>(8);


    public ProducerTemplate(Collection<ConfigUnit> configs) {
        for (ConfigUnit cfg : configs) {
            Map<String, Object> map = cfg.getCommon().toMap();
            map.putAll(cfg.getProducer().toMap());
            Producer<K, V> producer = new KafkaProducer<>(map);

            set.add(producer);

            for (String t : cfg.topics()) {
                producers.put(t, producer);
            }
        }
    }


    public void send(String topic, K key, V value) {
        this.send(topic, key, value, null);
    }


    public void send(String topic, V value) {
        this.send(topic, null, value, null);
    }


    public void send(String topic, V value, Callback callback) {
        this.send(topic, null, value, callback);
    }


    public void send(String topic, K key, V value, Callback callback) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
        Producer<K, V> producer = producers.get(topic);
        if (null == producer) {
            throw new KafkaConfigException("Cannot find producer for topic:" + topic);
        }
        producer.send(record, callback);
    }


    @Override
    public void shutdown() throws Exception {
        if (isRunning.compareAndSet(true, false)) {
            try {
                for (Producer<K, V> p : set) {
                    p.close();
                }
            } catch (Exception e) {
                throw new KafkaException(e);
            }
            shutdownLatch.countDown();
        }
    }


    @Override
    public void awaitShutdown() throws Exception {
        try {
            shutdownLatch.await();
        } catch (Exception e) {
            throw new KafkaException(e);
        }
    }


    @Override
    public void destroy() throws Exception {
        shutdown();
        awaitShutdown();
    }
}
