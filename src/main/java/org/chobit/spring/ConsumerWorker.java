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
 * Kafka topic消费执行者
 *
 * @author robin
 */
public final class ConsumerWorker<K, V> implements Runnable, Shutdown {


    private static final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

    /**
     * Kafka消费者
     */
    private final KafkaConsumer<K, V> consumer;

    /**
     * Kafka消息处理器，承担了消息处理逻辑
     */
    private final Processor<K, V> processor;

    /**
     * 消费组ID
     */
    private final String groupId;

    /**
     * Kafka topics
     */
    private final List<String> topics;

    /**
     * 消费poll等待时长
     */
    private final long pollTimeoutMs;

    /**
     * 消费close等待时长
     */
    private final long closeTimeoutMs;

    /**
     * 消费运行标识符
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 并发计数器
     */
    private final CountDownLatch startupLatch;

    /**
     * 构造器
     *
     * @param props       消费配置
     * @param consumerCfg 消费者配置
     * @param processor   处理器
     * @param groupId     消费组ID
     * @param topics      消费的topic
     * @param latch       计数器
     */
    public ConsumerWorker(Map<String, Object> props,
                          Consumer consumerCfg,
                          Processor<K, V> processor,
                          String groupId,
                          List<String> topics,
                          CountDownLatch latch) {

        this.consumer = new KafkaConsumer<>(props);

        this.groupId = groupId;
        this.topics = topics;
        this.pollTimeoutMs = consumerCfg.getPollTimeoutMs();
        this.closeTimeoutMs = consumerCfg.getCloseTimeoutMs();

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
