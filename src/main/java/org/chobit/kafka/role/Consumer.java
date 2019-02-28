package org.chobit.kafka.role;

import kafka.serializer.Decoder;
import org.chobit.kafka.Processor;
import org.chobit.kafka.serializer.StringSerializer;

import java.util.List;
import java.util.Properties;

public class Consumer {

    private String groupId;

    private String zookeeper;

    private Class<? extends Processor> processor;

    private Class<? extends Decoder> keySerializer;

    private Class<? extends Decoder> serializer = StringSerializer.class;

    private List<Topic> topics;

    private boolean autoCommitEnable = true;

    private int autoCommitInterval;

    private int rebalanceRetriesMax;

    private int rebalanceBackoff;

    private int queuedChunksMax;

    private Properties properties;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public Class<? extends Processor> getProcessor() {
        return processor;
    }

    public void setProcessor(Class<? extends Processor> processor) {
        this.processor = processor;
    }

    public Class<? extends Decoder> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Class<? extends Decoder> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Class<? extends Decoder> getSerializer() {
        return serializer;
    }

    public void setSerializer(Class<? extends Decoder> serializer) {
        this.serializer = serializer;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public boolean isAutoCommitEnable() {
        return autoCommitEnable;
    }

    public void setAutoCommitEnable(boolean autoCommitEnable) {
        this.autoCommitEnable = autoCommitEnable;
    }

    public int getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public void setAutoCommitInterval(int autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }

    public int getRebalanceRetriesMax() {
        return rebalanceRetriesMax;
    }

    public void setRebalanceRetriesMax(int rebalanceRetriesMax) {
        this.rebalanceRetriesMax = rebalanceRetriesMax;
    }

    public int getRebalanceBackoff() {
        return rebalanceBackoff;
    }

    public void setRebalanceBackoff(int rebalanceBackoff) {
        this.rebalanceBackoff = rebalanceBackoff;
    }

    public int getQueuedChunksMax() {
        return queuedChunksMax;
    }

    public void setQueuedChunksMax(int queuedChunksMax) {
        this.queuedChunksMax = queuedChunksMax;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
