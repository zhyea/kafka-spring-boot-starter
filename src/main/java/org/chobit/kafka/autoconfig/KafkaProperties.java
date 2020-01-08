package org.chobit.kafka.autoconfig;


import org.chobit.kafka.exception.KafkaConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static org.chobit.kafka.utils.Strings.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Kafka配置参数管理类
 *
 * @author zhangrui137
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties implements InitializingBean {

    private Properties common;

    private List<Consumer> consumers;

    private Producer producer;


    public Properties getCommon() {
        return common;
    }

    public void setCommon(Properties common) {
        this.common = common;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        check();
    }

    private void check() {
        if (isBlank(common.getBootstrapServers())) {
            throw new KafkaConfigException("Cannot find valid bootStrapServers");
        }
        if (null != consumers) {
            for (Consumer c : consumers) {
                if (isBlank(c.getGroupId())) {
                    throw new KafkaConfigException("Consumer groupId cannot be null.");
                }
                if (isBlank(c.getProcessor())) {
                    throw new KafkaConfigException("Processor for consumer group:" + c.getGroupId() + " cannot be null.");
                }
                if (isEmpty(c.getTopics())) {
                    throw new KafkaConfigException("Topics for consumer group:" + c.getGroupId() + " cannot be empty.");
                }
            }
        }
        if (null != producer && isBlank(producer.getBootstrapServers())) {
            producer.setBootstrapServers(common.getBootstrapServers());
        }
    }
}
