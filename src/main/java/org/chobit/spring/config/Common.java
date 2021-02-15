package org.chobit.spring.config;

import java.util.Map;

/**
 * 一些通用配置
 *
 * @author robin
 */
public class Common {

    /**
     * 通用消费者配置
     */
    private Map<String, Object> consumer;

    /**
     * 通用生产者配置
     */
    private Map<String, Object> producer;


    public Map<String, Object> getConsumer() {
        return consumer;
    }

    public void setConsumer(Map<String, Object> consumer) {
        this.consumer = consumer;
    }

    public Map<String, Object> getProducer() {
        return producer;
    }

    public void setProducer(Map<String, Object> producer) {
        this.producer = producer;
    }
}
