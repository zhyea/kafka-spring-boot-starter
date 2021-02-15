package org.chobit.spring.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 消费者配置
 *
 * @author robin
 */
public class Consumer {

    /**
     * 消费者执行逻辑器
     */
    private String processor;

    /**
     * poll等待时间
     */
    private long pollTimeoutMs = 5 * 1000;

    /**
     * close等待时间
     */
    private long closeTimeoutMs = 30 * 1000;

    /**
     * 单个应用中的消费者个数
     * <p>
     * 注意：消费者总数不能过partition总数
     */
    private int count;

    /**
     * 在无提交的offset时，consumer消费开始的位置
     */
    private AutoOffset autoOffsetReset = AutoOffset.latest;

    /**
     * 控制单次调用call方法能够返回的记录数量，帮助控制在轮询里需要处理的数据量。
     */
    private int maxPollRecords = 10000;

    /**
     * 指定了消费者是否自动提交offset，默认值是true，
     * <p>
     * 如果设置为false，需要自己控制何时提交offset。
     * 如果设置为true，可以通过设置 auto.commit.interval.ms属性来控制提交的频率
     */
    private boolean enableAutoCommit = true;

    /**
     * 提交offset的频率
     */
    private int autoCommitIntervalMs = 1000;

    /**
     * 该属性指定了当消费者被认为已经挂掉之前可以与服务器断开连接的时间。
     * <p>
     * 默认是3s，消费者在3s之内没有再次向服务器发送心跳，那么将会被认为已经死亡。此时，协调器将会出发再均衡，把它的分区分配给其他的消费者
     */
    private int sessionTimeoutMs = 15000;

    /**
     * 获取topic元数据失败重试时等待时间
     */
    private long retryBackoffMs = 1000;

    /**
     * 消费者配置信息
     */
    private Map<String, Object> props = new HashMap<>(8);

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
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

    public AutoOffset getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(AutoOffset autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public int getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(int maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public boolean isEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public int getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    public void setAutoCommitIntervalMs(int autoCommitIntervalMs) {
        this.autoCommitIntervalMs = autoCommitIntervalMs;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
