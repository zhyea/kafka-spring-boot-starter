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
 * kafka生产者快速访问类
 *
 * @author robin
 */
public class ProducerTemplate<K, V> implements Shutdown, DisposableBean {


    /**
     * shutdown 标识
     */
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    /**
     * 标记是否初始化
     */
    private final AtomicBoolean isInit = new AtomicBoolean(false);

    /**
     * topic -> Producer 映射
     */
    private final Map<String, Producer<K, V>> producers = new HashMap<>(8);

    /**
     * Producer集合
     */
    private final Set<Producer<K, V>> set = new HashSet<>(8);


    /**
     * 构造器
     *
     * @param commonConfig 通用配置
     * @param configs      具体配置集合
     */
    public ProducerTemplate(Map<String, Object> commonConfig,
                            Collection<ConfigUnit> configs) {
        for (ConfigUnit cfg : configs) {
            Map<String, Object> map = new HashMap<>(8);
            if (null != commonConfig && !commonConfig.isEmpty()) {
                map.putAll(commonConfig);
            }
            map.putAll(cfg.producerConfig());
            Producer<K, V> producer = new KafkaProducer<>(map);

            set.add(producer);

            for (String t : cfg.getTopics()) {
                producers.put(t, producer);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param topic kafka topic
     * @param key   分区key
     * @param value 消息
     */
    public void send(String topic, K key, V value) {
        this.send(topic, key, value, null);
    }

    /**
     * 发送消息
     *
     * @param topic Kafka topic
     * @param value 消息
     */
    public void send(String topic, V value) {
        this.send(topic, null, value, null);
    }

    /**
     * 发送消息
     *
     * @param topic    Kafka topic
     * @param value    消息
     * @param callback 回传函数
     */
    public void send(String topic, V value, Callback callback) {
        this.send(topic, null, value, callback);
    }

    /**
     * 发送消息
     *
     * @param topic    Kafka topic
     * @param key      分区key
     * @param value    消息
     * @param callback 回传函数
     */
    public void send(String topic, K key, V value, Callback callback) {
        Producer<K, V> producer = producers.get(topic);
        if (null == producer) {
            throw new KafkaConfigException("Cannot find producer for topic:" + topic);
        }
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
        producer.send(record, callback);
    }


    @Override
    public void shutdown() throws Exception {
        if (isInit.compareAndSet(true, false)) {
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
