package org.chobit.spring.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * 配置单元，标识一组相同服务器，可以采用近似处理逻辑的kafka日志集合
 *
 * @author robin
 */
public class ConfigUnit {

    /**
     * 配置ID，兼用做Consumer groupID
     */
    private String groupId;

    /**
     * kafka 连接信息
     */
    private String bootstrapServers;

    /**
     * kafka topic集合
     */
    private List<String> topics;

    /**
     * 消费者配置
     */
    private Consumer consumer;

    /**
     * 生产者配置
     */
    private Producer producer;

    /**
     * 消费者配置信息
     *
     * @return 消费者配置
     */
    public Map<String, Object> consumerConfig() {

        Map<String, Object> config = new HashMap<>(8);

        config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(GROUP_ID_CONFIG, groupId);

        if (null != consumer && null != consumer.getKeyDeserializer()) {
            config.put(KEY_DESERIALIZER_CLASS_CONFIG, consumer.getKeyDeserializer());
        } else {
            config.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        }

        if (null != consumer && null != consumer.getValueDeserializer()) {
            config.put(VALUE_DESERIALIZER_CLASS_CONFIG, consumer.getValueDeserializer());
        } else {
            config.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        }

        if (null != consumer) {
            if (null != consumer.getAutoOffsetReset()) {
                config.put(AUTO_OFFSET_RESET_CONFIG, consumer.getAutoOffsetReset().name());
            }
            config.put(ENABLE_AUTO_COMMIT_CONFIG, consumer.isEnableAutoCommit());
            if (consumer.getMaxPollRecords() > 0) {
                config.put(MAX_POLL_RECORDS_CONFIG, consumer.getMaxPollRecords());
            }
            if (consumer.getAutoCommitIntervalMs() > 0) {
                config.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, consumer.getAutoCommitIntervalMs());
            }
            if (consumer.getSessionTimeoutMs() > 0) {
                config.put(SESSION_TIMEOUT_MS_CONFIG, consumer.getSessionTimeoutMs());
            }
            if (consumer.getRetryBackoffMs() > 0) {
                config.put(RETRY_BACKOFF_MS_CONFIG, consumer.getRetryBackoffMs());
            }
            if (null != consumer.getProps()) {
                config.putAll(consumer.getProps());
            }
        }

        return config;
    }

    /**
     * 生产者配置信息
     *
     * @return 生产者配置
     */
    public Map<String, Object> producerConfig() {

        Map<String, Object> config = new HashMap<>(8);

        config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        if (null != producer && null != producer.getKeySerializer()) {
            config.put(KEY_SERIALIZER_CLASS_CONFIG, consumer.getKeyDeserializer());
        } else {
            config.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        }

        if (null != producer && null != producer.getValueSerializer()) {
            config.put(VALUE_SERIALIZER_CLASS_CONFIG, consumer.getValueDeserializer());
        } else {
            config.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        }

        if (null != producer && null != producer.getProps()) {
            config.putAll(producer.getProps());
        }
        return config;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }
}
