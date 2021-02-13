package org.chobit.spring.config;

import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.List;

/**
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
     * key序列化类
     */
    private Class<?> keyDeserializer = StringDeserializer.class;

    /**
     * value序列化类
     */
    private Class<?> valueDeserializer = StringDeserializer.class;

    /**
     * 消费者配置
     */
    private Consumer consumer;

    /**
     * 生产者配置
     */
    private Producer producer;


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

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }
}
