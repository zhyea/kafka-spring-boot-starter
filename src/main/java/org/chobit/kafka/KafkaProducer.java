package org.chobit.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.chobit.kafka.config.Common;
import org.chobit.kafka.config.Producer;
import org.chobit.kafka.exception.KafkaException;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

;

/**
 * Kafka生产者工具类
 *
 * @author robin
 */
public final class KafkaProducer<K, V> implements Shutdown, DisposableBean {

    private final org.apache.kafka.clients.producer.Producer producer;

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    public KafkaProducer(Producer producerConfig, Common commonConfig) {
        Map<String, Object> cfg = commonConfig.toMap();
        cfg.putAll(producerConfig.toMap());
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(cfg);
        this.isRunning.set(true);
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
        producer.send(record, callback);
    }


    @Override
    public void shutdown() {
        if (isRunning.compareAndSet(true, false)) {
            try {
                producer.close();
            } catch (Exception e) {
                throw new KafkaException(e);
            }
            shutdownLatch.countDown();
        }
    }

    @Override
    public void awaitShutdown() {
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
