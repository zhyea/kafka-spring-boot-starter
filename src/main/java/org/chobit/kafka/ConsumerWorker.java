package org.chobit.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;
import org.chobit.kafka.autoconfig.ConsumerConfigWrapper;
import org.chobit.kafka.exception.KafkaConsumeException;
import org.chobit.kafka.role.Topic;
import org.chobit.kafka.utils.Threads;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Kafka topic 消费类
 */
public class ConsumerWorker extends AbstractConsumeThread implements Shutdown {

    private final String groupId;
    private final ConsumerConnector consumer;
    private final ExecutorService executorService;
    private final HashMap<String, Integer> topicCountMap;
    private final Decoder valSerializer;
    private final Decoder keySerializer;
    private final Processor processor;

    public ConsumerWorker(ConsumerConfigWrapper configWrapper, Processor processor) {
        this.groupId = configWrapper.getGroupId();
        this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(configWrapper.getConfig());
        String threadNamePattern = "Consumer-thread-for-topic-" + configWrapper.getGroupId() + "-%d";
        int threadsNum = configWrapper.consumerThreadsNum();
        this.executorService = Threads.newFixCachedThreadPool(threadsNum, threadNamePattern, false);
        this.topicCountMap = new HashMap<String, Integer>(configWrapper.getTopics().size());
        for (Topic t : configWrapper.getTopics()) {
            this.topicCountMap.put(t.getName(), t.getThreadNum());
        }
        this.keySerializer = configWrapper.getKeySerializer();
        this.valSerializer = configWrapper.getValueSerializer();
        this.processor = processor;
    }


    @Override
    public void run() {
        startupComplete();

        Map<String, List<KafkaStream>> topicStreamMap =
                consumer.createMessageStreams(topicCountMap, keySerializer, valSerializer);

        for (Map.Entry<String, List<KafkaStream>> entry : topicStreamMap.entrySet()) {
            for (final KafkaStream stream : entry.getValue()) {
                ConsumerThread t = new ConsumerThread(entry.getKey(), stream, this.processor);
                executorService.submit(t);
                t.awaitStartup();
            }
        }

        logger.info("Consumer group:{} has been started.", groupId);
    }


    public void shutdown() {
        if (null != consumer) consumer.shutdown();
        if (null != executorService) executorService.shutdown();
        super.shutdownComplete();
    }


    @Override
    public void awaitShutdown() {
        try {
            if (null != executorService) executorService.awaitTermination(5, TimeUnit.MINUTES);
            super.awaitShutdown();
        } catch (InterruptedException e) {
            throw new KafkaConsumeException("Kafka consumer shutdown failed.", e);
        }
    }


    /**
     * Kafka分区消费线程
     */
    private static class ConsumerThread extends AbstractConsumeThread {

        private final String topic;
        private final KafkaStream stream;
        private final Processor processor;

        ConsumerThread(String topic,
                       KafkaStream stream,
                       Processor processor) {
            this.topic = topic;
            this.stream = stream;
            this.processor = processor;
        }

        @Override
        public void run() {
            startupComplete();
            try {
                ConsumerIterator itr = stream.iterator();
                while (itr.hasNext()) {
                    Object message = itr.next().message();
                    processor.process(topic, message);
                }
            } catch (Throwable t) {
                throw new KafkaConsumeException(t);
            } finally {
                processor.shutdown();
                processor.awaitShutdown();
                shutdownComplete();
                logger.info("Consumer thread:{} has been shutdown", Thread.currentThread().getName());
            }
        }
    }


}
