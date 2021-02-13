package org.chobit.spring;


import org.chobit.spring.config.Common;
import org.chobit.spring.config.ConfigUnit;
import org.chobit.spring.config.Consumer;
import org.chobit.spring.exception.KafkaConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.chobit.spring.StringKit.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Kafka配置参数管理类
 *
 * @author robin
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties implements InitializingBean {


    private Common common;


    private Map<String, ConfigUnit> config;


    public Map<String, ConfigUnit> getConfig() {
        return config;
    }

    public void setConfig(Map<String, ConfigUnit> config) {
        this.config = config;
    }

    public Collection<ConfigUnit> configs() {
        return this.config.values();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == this.config) {
            this.config = new HashMap<>(8);
        }
        for (Map.Entry<String, ConfigUnit> ele : this.config.entrySet()) {
            String groupId = ele.getKey();
            ConfigUnit cfg = ele.getValue();
            cfg.setGroupId(groupId);
            check(cfg);
        }
    }

    private void check(ConfigUnit config) {
        if (isBlank(config.getGroupId())) {
            throw new KafkaConfigException("Id of kafka config unit is necessary");
        }
        if (null == config.getCommon() || isBlank(config.getCommon().getBootstrapServers())) {
            throw new KafkaConfigException("Cannot find valid bootstrap-servers for " + config.getGroupId());
        }
        if (null != config.getConsumer()) {
            for (Consumer c : config.getConsumer()) {
                if (isBlank(c.getGroupId())) {
                    throw new KafkaConfigException("Consumer group-id cannot be null.");
                }
                if (isBlank(c.getProcessor())) {
                    throw new KafkaConfigException("Processor for consumer group:" + c.getGroupId() + " cannot be null.");
                }
                if (isEmpty(c.getTopics())) {
                    throw new KafkaConfigException("Topics for consumer group:" + c.getGroupId() + " cannot be empty.");
                }
            }
        }
    }
}
