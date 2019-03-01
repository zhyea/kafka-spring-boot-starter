package org.chobit.kafka;

import kafka.javaapi.producer.Producer;
import org.chobit.kafka.autoconfig.ProducerConfigWrapper;
import org.chobit.kafka.exception.KafkaException;

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


    public <K, V> KafkaProducer<K, V> getById(String id) {
        if (null == producers || producers.isEmpty()) {
            throw new KafkaException("None KafkaProducer Instance can be found");
        }
        if (!producers.containsKey(id)) {
            throw new KafkaException("KafkaProducer with id:" + id + " cannot be found");
        }
        return producers.get(id);
    }


    public <K, V> KafkaProducer<K, V> getRandom() {
        if (null == producers || producers.isEmpty()) {
            throw new KafkaException("None KafkaProducer Instance can be found");
        }
        return producers.entrySet().iterator().next().getValue();
    }
}
