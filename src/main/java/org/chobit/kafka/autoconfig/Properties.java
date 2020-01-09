package org.chobit.kafka.autoconfig;

import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.chobit.kafka.utils.Strings.isNotBlank;

/**
 * 基础配置
 *
 * @author robin
 */
public class Properties {

    /**
     * kafka 连接信息
     */
    private String bootstrapServers;
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
     *
     */
    private Class<?> keyDeserializer = StringDeserializer.class;
    /**
     *
     */
    private Class<?> valueDeserializer = StringDeserializer.class;
    /**
     * 在无提交的offset时，consumer消费开始的位置
     */
    private AutoOffset autoOffsetReset = AutoOffset.latest;


    public final Map<String, Object> toMap() {
        Map<String, Object> config = new HashMap<>(8);

        if (isNotBlank(bootstrapServers)) {
            config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }
        config.put(ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        if (maxPollRecords > 0) {
            config.put(MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        }
        if (autoCommitIntervalMs > 0) {
            config.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        }
        if (sessionTimeoutMs > 0) {
            config.put(SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        }
        if (null != keyDeserializer) {
            config.put(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        }
        if (null != valueDeserializer) {
            config.put(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        }
        if (null != autoOffsetReset) {
            config.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset.name());
        }
        if (retryBackoffMs > 0) {
            config.put(RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        }

        return config;
    }


    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
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

    public Class<?> getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(Class<?> keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public Class<?> getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(Class<?> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public AutoOffset getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(AutoOffset autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

}
