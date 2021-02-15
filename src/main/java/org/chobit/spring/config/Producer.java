package org.chobit.spring.config;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产者配置
 *
 * @author robin
 */
public class Producer {


    /**
     * key反序列化类
     */
    private Class<? extends Serializer<?>> keySerializer = StringSerializer.class;

    /**
     * value反序列化类
     */
    private Class<? extends Serializer<?>> valueSerializer = StringSerializer.class;

    /**
     * 生产者配置信息
     */
    private Map<String, Object> props = new HashMap<>(8);


    public Class<? extends Serializer<?>> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Class<? extends Serializer<?>> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Class<? extends Serializer<?>> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Class<? extends Serializer<?>> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
