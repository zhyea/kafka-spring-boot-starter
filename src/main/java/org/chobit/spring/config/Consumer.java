package org.chobit.spring.config;

/**
 * 消费者配置
 *
 * @author robin
 */
public class Consumer {

    private String processor;

    private long pollTimeoutMs = 5 * 1000;

    private long closeTimeoutMs = 30 * 1000;

    /**
     * 单个应用中的消费者个数
     * <p>
     * 注意：消费者总数不能过partition总数
     */
    private int count;


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
}
