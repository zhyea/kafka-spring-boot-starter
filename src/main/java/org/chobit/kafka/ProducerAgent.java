package org.chobit.kafka;

import org.chobit.kafka.config.ConfigUnit;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProducerAgent {


    private final Map<String, KafkaProducer<?, ?>> map = new LinkedHashMap<>(8);


    public ProducerAgent(List<ConfigUnit> configs) {
        configs.forEach(cfg -> {
            org.chobit.kafka.KafkaProducer<?, ?> producer = new org.chobit.kafka.KafkaProducer<>(cfg.getProducer(), cfg.getCommon());
            map.put(cfg.getId(), producer);
        });
    }


    public KafkaProducer<?, ?> get(String id) {
        return map.get(id);
    }

    public KafkaProducer<?, ?> first() {
        if (map.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, KafkaProducer<?, ?>> entry : map.entrySet()) {
            return entry.getValue();
        }

        return null;
    }


    public Collection<KafkaProducer<?, ?>> all() {
        return map.values();
    }

}
