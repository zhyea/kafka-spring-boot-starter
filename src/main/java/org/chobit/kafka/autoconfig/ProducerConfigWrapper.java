package org.chobit.kafka.autoconfig;

import kafka.producer.ProducerConfig;

/**
 * producer config wrapper
 */
public final class ProducerConfigWrapper {

    private String id;

    private ProducerConfig config;

    public ProducerConfigWrapper(String id, ProducerConfig config) {
        this.id = id;
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProducerConfig getConfig() {
        return config;
    }

    public void setConfig(ProducerConfig config) {
        this.config = config;
    }
}
