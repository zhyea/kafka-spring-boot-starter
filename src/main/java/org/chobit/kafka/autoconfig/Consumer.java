package org.chobit.kafka.autoconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

/**
 * 消费者配置
 *
 * @author robin
 */
public class Consumer {

    private String groupId;

    private String processor;

    private List<String> topics;

    private long pollTimeoutMs = 5 * 1000;

    private long closeTimeoutMs = 30 * 1000;
    /**
     * 单个应用中的消费者个数
     * <p>
     * 注意：消费者总数不能过partition总数
     */
    private int count;


    public final Map<String, Object> toMap() {
        Map<String, Object> config = new HashMap<>(4);

        config.put(GROUP_ID_CONFIG, groupId);

        return config;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public long getPollTimeoutMs() {
        return pollTimeoutMs;
    }

    public void setPollTimeoutMs(long pollTimeoutMs) {
        this.pollTimeoutMs = pollTimeoutMs;
    }

    public long getCloseTimeoutMs() {
        return closeTimeoutMs;
    }

    public void setCloseTimeoutMs(long closeTimeoutMs) {
        this.closeTimeoutMs = closeTimeoutMs;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
