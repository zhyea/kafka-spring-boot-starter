package org.chobit.kafka.autoconfig;


import org.chobit.kafka.role.ZooKeeper;
import org.chobit.kafka.role.Consumer;
import org.chobit.kafka.role.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private boolean autoStart;

    private List<ZooKeeper> zookeepers;

    private List<Consumer> consumers;

    private List<Producer> producers;


    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public List<ZooKeeper> getZookeepers() {
        return zookeepers;
    }

    public void setZookeepers(List<ZooKeeper> zookeepers) {
        this.zookeepers = zookeepers;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }
}
