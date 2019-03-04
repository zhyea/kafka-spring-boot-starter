package org.chobit.kafka.role;

import kafka.producer.Partitioner;
import kafka.serializer.Encoder;
import kafka.serializer.StringEncoder;
import org.chobit.kafka.partitioner.RollingPartitioner;

import java.util.Properties;

public class Producer {

    private String id;

    private String zookeeper;

    private ProducerType type = ProducerType.sync;

    private Class<? extends Encoder> keySerializer;

    private Class<? extends Encoder> serializer = StringEncoder.class;

    private Class<? extends Partitioner> partitioner = RollingPartitioner.class;

    private long bufferSize = 102400L;

    private Properties properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public ProducerType getType() {
        return type;
    }

    public void setType(ProducerType type) {
        this.type = type;
    }

    public Class<? extends Encoder> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Class<? extends Encoder> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Class<? extends Encoder> getSerializer() {
        return serializer;
    }

    public void setSerializer(Class<? extends Encoder> serializer) {
        this.serializer = serializer;
    }

    public Class<? extends Partitioner> getPartitioner() {
        return partitioner;
    }

    public void setPartitioner(Class<? extends Partitioner> partitioner) {
        this.partitioner = partitioner;
    }

    public long getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(long bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
