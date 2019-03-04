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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.chobit.kafka.role.ProducerType.sync;
import static org.chobit.kafka.utils.Collections.isEmpty;
import static org.chobit.kafka.utils.JsonUtils.fromJson;
import static org.chobit.kafka.utils.ReflectUtils.newInstance;
import static org.chobit.kafka.utils.StringUtils.isBlank;
import static org.chobit.kafka.utils.StringUtils.uuid;

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

            if (isBlank(p.getId())) {
                p.setId(uuid());
            }

            Properties props = new Properties();
            if (p.getBufferSize() > 0) {
                props.put("send.buffer.bytes", String.valueOf(p.getBufferSize()));
            }

            if (null != p.getType() && sync == p.getType()) {
                props.put("request.required.acks", String.valueOf(0));
            }

            if (!isEmpty(p.getProperties())) {
                props.putAll(p.getProperties());
            }

            ZooKeeper zk = zkMap.get(p.getZookeeper());
            String brokers = obtainBrokers(zk);
            props.put("metadata.broker.list", brokers);

            if (null != p.getSerializer()) {
                props.put("serializer.class", p.getSerializer().getName());
            }
            if (null != p.getKeySerializer()) {
                props.put("key.serializer.class", p.getKeySerializer().getName());
            }
            if (null == p.getPartitioner()) {
                props.put("partitioner.class", p.getPartitioner().getName());
            }
            if (null != p.getType()) {
                props.put("producer.type", p.getType().name());
            }
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
