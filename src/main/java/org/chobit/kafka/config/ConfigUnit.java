package org.chobit.kafka.config;

import org.springframework.lang.NonNull;

import java.util.List;

public class ConfigUnit {

    @NonNull
    private String id;

    private Common common;

    private List<Consumer> consumers;

    private Producer producer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }
}
