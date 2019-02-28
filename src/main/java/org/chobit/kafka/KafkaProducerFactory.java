package org.chobit.kafka;

import kafka.javaapi.producer.Producer;
import org.chobit.kafka.autoconfig.ProducerConfigWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaProducerFactory {

    private final Map<String, KafkaProducer> producers;


    public KafkaProducerFactory(List<ProducerConfigWrapper> configs) {
        this.producers = new ConcurrentHashMap<String, KafkaProducer>(configs.size());

        for (ProducerConfigWrapper pcw : configs) {
            KafkaProducer producer = new KafkaProducer(new Producer(pcw.getConfig()));
            producers.put(pcw.getId(), producer);
        }
    }


    public <K, V> KafkaProducer<K, V> getById(String id){
        return producers.get(id);
    }
}
