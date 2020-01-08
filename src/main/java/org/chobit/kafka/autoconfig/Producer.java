package org.chobit.kafka.autoconfig;


import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * Producer配置信息
 *
 * @author robin
 */
public class Producer {

    private boolean enable = true;

    private String bootstrapServers;

    private Class<?> keySerializer = StringSerializer.class;

    private Class<?> valueSerializer = StringSerializer.class;


    public final Map<String, Object> toMap() {
        Map<String, Object> config = new HashMap<>(8);

        config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getName());
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getName());

        return config;
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Class<?> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Class<?> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Class<?> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Class<?> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }
}
