package org.chobit.kafka.autoconfig;

import kafka.consumer.ConsumerConfig;
import kafka.serializer.Decoder;
import org.chobit.kafka.Processor;
import org.chobit.kafka.role.Topic;

import java.util.List;

/**
 * consumer config wrapper
 */
public final class ConsumerConfigWrapper {

    private String groupId;

    private Class<? extends Processor> processor;

    private Decoder keySerializer;

    private Decoder valueSerializer;

    private List<Topic> topics;

    private ConsumerConfig config;

    public ConsumerConfigWrapper(String groupId,
                                 Class<? extends Processor> processor,
                                 Decoder keySerializer,
                                 Decoder valueSerializer,
                                 List<Topic> topics, ConsumerConfig config) {
        this.groupId = groupId;
        this.processor = processor;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.topics = topics;
        this.config = config;
    }


    public int consumerThreadsNum() {
        int count = 0;
        for (Topic t : topics) {
            count += t.getThreadNum();
        }
        return count;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Class<? extends Processor> getProcessor() {
        return processor;
    }

    public void setProcessor(Class<? extends Processor> processor) {
        this.processor = processor;
    }

    public Decoder getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Decoder keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Decoder getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Decoder valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public ConsumerConfig getConfig() {
        return config;
    }

    public void setConfig(ConsumerConfig config) {
        this.config = config;
    }
}
