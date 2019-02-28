package org.chobit.kafka.autoconfig;

import java.util.List;

public final class KafkaConfig {

    private boolean autoStart;

    private List<ConsumerConfigWrapper> consumers;

    private List<ProducerConfigWrapper> producers;

    public KafkaConfig(boolean autoStart,
                       List<ConsumerConfigWrapper> consumers,
                       List<ProducerConfigWrapper> producers) {
        this.autoStart = autoStart;
        this.consumers = consumers;
        this.producers = producers;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public List<ConsumerConfigWrapper> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<ConsumerConfigWrapper> consumers) {
        this.consumers = consumers;
    }

    public List<ProducerConfigWrapper> getProducers() {
        return producers;
    }

    public void setProducers(List<ProducerConfigWrapper> producers) {
        this.producers = producers;
    }
}


