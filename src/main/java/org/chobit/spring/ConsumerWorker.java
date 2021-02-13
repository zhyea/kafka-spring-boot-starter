package org.chobit.spring;


import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.chobit.spring.config.Consumer;
import org.chobit.spring.exception.KafkaConsumerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kafka topic 消费类
 *
 * @author robin
 */
public final class ConsumerWorker<K, V> implements Runnable, Shutdown {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

    private final KafkaConsumer<K, V> consumer;

    private final Processor<K, V> processor;

    private final String groupId;

    private final List<String> topics;

    private final long pollTimeoutMs;

    private final long closeTimeoutMs;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final CountDownLatch startupLatch;

    public ConsumerWorker(Map<String, Object> config,
                          Consumer consumerConfig,
                          Processor<K, V> processor,
                          CountDownLatch latch) {

        config.putAll(consumerConfig.toMap());

        this.consumer = new KafkaConsumer<>(config);

        this.groupId = consumerConfig.getGroupId();
        this.topics = consumerConfig.getTopics();
        this.pollTimeoutMs = consumerConfig.getPollTimeoutMs();
        this.closeTimeoutMs = consumerConfig.getCloseTimeoutMs();

        this.processor = processor;

        this.startupLatch = latch;
    }


    @Override
    public void run() {
        try {
            this.consumer.subscribe(this.topics);

            this.isRunning.set(true);

            this.startupLatch.countDown();

            logger.info("Consumer group:{} has been started.", groupId);

            while (isRunning.get()) {
                ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));
                // 如果无法读取数据，sleep一段时间，避免频繁请求
                if (records.isEmpty()) {
                    TimeUnit.SECONDS.sleep(1L);
                }
                try {
                    processor.process(records);
                } catch (Throwable t) {
                    shutdown();
                    awaitShutdown();
                    logger.error("Consumer of group:{} occurred error when processing messages", groupId, t);
                    throw t;
                }
            }
        } catch (Throwable t) {
            logger.error("Consumer of group:{} run into error", groupId, t);
            throw new KafkaConsumerException(t);
        } finally {
            if (null != consumer) {
                consumer.close(Duration.ofMillis(closeTimeoutMs));
            }
            shutdown();
            awaitShutdown();
            logger.info("Consumer thread:{} has been shutdown.", Thread.currentThread().getName());
        }

    }


    @Override
    public void shutdown() {
        isRunning.set(false);
        if (null != processor) {
            try {
                processor.shutdown();
            } catch (Exception e) {
                logger.error("Processor for kafka consumer(group id:{}) shutdown error", groupId, e);
            }
        }
    }


    @Override
    public void awaitShutdown() {
        if (null != processor) {
            try {
                processor.awaitShutdown();
            } catch (Exception e) {
                logger.error("Processor for kafka consumer(group id:{}) await shutdown error", groupId, e);
            }
        }
    }

}
