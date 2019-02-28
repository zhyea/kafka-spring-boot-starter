package org.chobit.kafka.autoconfig;

import kafka.consumer.ConsumerConfig;
import kafka.producer.ProducerConfig;
import kafka.serializer.Decoder;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.chobit.kafka.exception.KafkaConfigException;
import org.chobit.kafka.role.Consumer;
import org.chobit.kafka.role.Producer;
import org.chobit.kafka.role.ZooKeeper;
import org.chobit.kafka.serializer.StringSerializer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.chobit.kafka.role.ProducerType.sync;
import static org.chobit.kafka.utils.Collections.isEmpty;
import static org.chobit.kafka.utils.JsonUtils.fromJson;
import static org.chobit.kafka.utils.ReflectUtils.newInstance;

final class KafkaConfigBuilder {


    KafkaConfig build(KafkaProperties props) {
        Map<String, ZooKeeper> zookeepers = null;
        List<ConsumerConfigWrapper> consumerConfigs = null;
        List<ProducerConfigWrapper> producerConfigs = null;

        if (!isEmpty(props.getZookeepers())) {
            zookeepers = zookeepers(props.getZookeepers());
        }

        if (!isEmpty(props.getConsumers())) {
            consumerConfigs = consumerConfigs(zookeepers, props.getConsumers());
        }
        if (!isEmpty(props.getProducers())) {
            producerConfigs = producerConfigs(zookeepers, props.getProducers());
        }
        return new KafkaConfig(props.isAutoStart(), consumerConfigs, producerConfigs);
    }


    private Map<String, ZooKeeper> zookeepers(List<ZooKeeper> zookeepers) {
        if (null == zookeepers || zookeepers.isEmpty()) {
            throw new KafkaConfigException("Zookeeper config is empty.");
        }
        Map<String, ZooKeeper> zkMap
                = new ConcurrentHashMap<String, ZooKeeper>(2);
        for (ZooKeeper zk : zookeepers) {
            zkMap.put(zk.getId(), zk);
        }
        return zkMap;
    }


    /**
     * consumer configs
     */
    private List<ConsumerConfigWrapper> consumerConfigs(Map<String, ZooKeeper> zkMap,
                                                        List<Consumer> consumers) {

        List<ConsumerConfigWrapper> configs = new ArrayList<ConsumerConfigWrapper>(2);
        for (Consumer cc : consumers) {

            ZooKeeper zk = zkMap.get(cc.getZookeeper());
            if (null == zk) {
                throw new KafkaConfigException(String.format("Finding zookeeper config with id:%s failed.", cc.getZookeeper()));
            }

            if (null == cc.getProcessor()) {
                throw new KafkaConfigException("Processor class must be configured.");
            }

            Properties props = new Properties();

            /* add zookeeper config */
            props.put("zookeeper.session.timeout.ms", String.valueOf(zk.getSessionTimeout()));
            props.put("zookeeper.connection.timeout.ms", String.valueOf(zk.getConnectTimeout()));
            props.put("zookeeper.connect", zk.getZkConnect());

            /* add consumer config */
            props.put("group.id", cc.getGroupId());
            props.put("auto.commit.enable", String.valueOf(cc.isAutoCommitEnable()));
            if (cc.getAutoCommitInterval() > 0) {
                props.put("auto.commit.interval.ms", String.valueOf(cc.getAutoCommitInterval()));
            }
            if (cc.getRebalanceRetriesMax() > 0) {
                props.put("rebalance.max.retries", String.valueOf(cc.getRebalanceRetriesMax()));
            }
            if (cc.getRebalanceBackoff() > 0) {
                props.put("rebalance.backoff.ms", String.valueOf(cc.getRebalanceBackoff()));
            }
            if (cc.getQueuedChunksMax() > 0) {
                props.put("queued.max.message.chunks", String.valueOf(cc.getQueuedChunksMax()));
            }

            /* add default config */
            props.put("auto.offset.reset", "largest");

            if (null != cc.getProperties()) {
                props.putAll(cc.getProperties());
            }

            Decoder keyDecoder = new StringSerializer();
            Decoder valDecoder = new StringSerializer();

            if (null != cc.getKeySerializer()) {
                keyDecoder = newInstance(cc.getKeySerializer());
            }
            if (null != cc.getKeySerializer()) {
                valDecoder = newInstance(cc.getSerializer());
            }

            configs.add(
                    new ConsumerConfigWrapper(
                            cc.getGroupId(),
                            cc.getProcessor(),
                            keyDecoder,
                            valDecoder,
                            cc.getTopics(), new ConsumerConfig(props)));
        }

        return configs;
    }

    /**
     * producer configs
     */
    private List<ProducerConfigWrapper> producerConfigs(Map<String, ZooKeeper> zkMap,
                                                        List<Producer> producers) {
        List<ProducerConfigWrapper> configs = new ArrayList<ProducerConfigWrapper>(2);

        for (Producer p : producers) {
            Properties props = new Properties();
            props.put("send.buffer.bytes", String.valueOf(p.getBufferSize()));
            props.put("compression.codec", p.getCompressionCodec());
            props.put("producer.type", p.getType());

            if (sync == p.getType()) {
                props.put("request.required.acks", String.valueOf(0));
            }

            if (!isEmpty(p.getProperties())) {
                props.putAll(p.getProperties());
            }

            ZooKeeper zk = zkMap.get(p.getZookeeper());
            String brokers = obtainBrokers(zk);
            props.put("metadata.broker.list", brokers);

            if (null == p.getSerializer()) {
                throw new KafkaConfigException("Serializer class of producer is empty");
            }
            props.put("serializer.class", p.getSerializer());
            if (null != p.getKeySerializer()) {
                props.put("key.serializer.class", p.getKeySerializer());
            }
            if (null == p.getPartitioner()) {
                throw new KafkaConfigException("Partitioner class of producer is empty");
            }
            props.put("partitioner.class", p.getPartitioner());
            props.put("producer.type", p.getType());
            props.put("compression.codec", p.getCompressionCodec());

            if (null != p.getProperties()) {
                props.putAll(p.getProperties());
            }

            configs.add(new ProducerConfigWrapper(p.getId(), new ProducerConfig(props)));
        }

        return configs;
    }


    /**
     * 通过zookeeper获取broker信息
     */
    private String obtainBrokers(ZooKeeper zk) {
        try {
            ZkClient zkClient = new ZkClient(zk.getZkConnect(), zk.getSessionTimeout(), zk.getConnectTimeout(), new BytesPushThroughSerializer());
            List<String> brokerList = zkClient.getChildren("/brokers/ids");

            StringBuilder builder = new StringBuilder();
            for (String brokerId : brokerList) {
                if (!brokerId.isEmpty()) {
                    builder.append(",");
                }
                byte[] bytes = zkClient.readData("/brokers/ids/" + brokerId);
                HostAndPort hp = fromJson(bytes, HostAndPort.class);
                builder.append(hp.getHost())
                        .append(":")
                        .append(hp.getPort());
            }
            return builder.toString();
        } catch (Exception e) {
            throw new KafkaConfigException("Read brokers from zookeeper failed.", e);
        }
    }
}
