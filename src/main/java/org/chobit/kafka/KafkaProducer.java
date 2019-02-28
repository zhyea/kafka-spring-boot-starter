package org.chobit.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.chobit.kafka.exception.KafkaException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaProducer<K, V> implements Shutdown {

    private final Producer producer;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public KafkaProducer(Producer producer) {
        this.producer = producer;
        this.isRunning.set(true);
    }

    public void send(String topic, K key, V data) {
        KeyedMessage<K, V> msg = new KeyedMessage<K, V>(topic, key, data);
        producer.send(msg);
    }

    public void send(String topic, V data) {
        KeyedMessage<K, V> msg = new KeyedMessage<K, V>(topic, null, data);
        producer.send(msg);
    }

    public void send(String topic, K key, List<V> dataList) {
        List<KeyedMessage<K, V>> msgList = new ArrayList<KeyedMessage<K, V>>();
        for (V val : dataList) {
            KeyedMessage<K, V> msg = new KeyedMessage<K, V>(topic, key, val);
            msgList.add(msg);
        }
        producer.send(msgList);
    }

    public void send(String topic, List<V> dataList) {
        List<KeyedMessage<K, V>> msgList = new ArrayList<KeyedMessage<K, V>>();
        for (V val : dataList) {
            KeyedMessage<K, V> msg = new KeyedMessage<K, V>(topic, null, val);
            msgList.add(msg);
        }
        producer.send(msgList);
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
}
