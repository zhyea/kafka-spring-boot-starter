package org.chobit.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.chobit.kafka.autoconfig.Consumer;
import org.chobit.kafka.exception.KafkaConsumerException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Kafka topic 消费类
 *
 * @author robin
 */
public final class ConsumerWorker<K, V> extends AbstractConsumerThread implements Shutdown {

    private final KafkaConsumer<K, V> consumer;

    private final Processor<K, V> processor;

    private final String groupId;

    private final List<String> topics;

    private final long pollTimeoutMs;

    private final long closeTimeoutMs;


    public ConsumerWorker(Map<String, Object> config,
                          Consumer consumerConfig,
                          Processor<K, V> processor) {

        config.putAll(consumerConfig.toMap());

        this.consumer = new KafkaConsumer<>(config);

        this.groupId = consumerConfig.getGroupId();
        this.topics = consumerConfig.getTopics();
        this.pollTimeoutMs = consumerConfig.getPollTimeoutMs();
        this.closeTimeoutMs = consumerConfig.getCloseTimeoutMs();

        this.processor = processor;
    }


    @Override
    public void run() {
        try {
            startupComplete();
            consumer.subscribe(this.topics);

            while (isRunning()) {
                ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));
                // 如果无法读取数据，sleep一段时间，避免频繁请求
                if (records.isEmpty()) {
                    TimeUnit.MINUTES.sleep(5);
                }
                processor.process(records);
            }
        } catch (Throwable t) {
            throw new KafkaConsumerException(t);
        } finally {
            awaitShutdown();
            shutdown();
            logger.info("Consumer thread:{} has been shutdown.", Thread.currentThread().getName());
        }

        logger.info("Consumer group:{} has been started.", groupId);
    }


    @Override
    public void shutdown() {
        if (null != consumer) {
            consumer.close(Duration.ofMillis(closeTimeoutMs));
        }
        if (null != processor) {
            processor.shutdown();
        }
        super.shutdownComplete();
    }


    @Override
    public void awaitShutdown() {
        super.awaitShutdown();
        if (null != consumer) {
            consumer.unsubscribe();
        }
        if (null != processor) {
            processor.awaitShutdown();
        }
    }

}
