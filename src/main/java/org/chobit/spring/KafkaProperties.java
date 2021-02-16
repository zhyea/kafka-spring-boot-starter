package org.chobit.spring;


import org.chobit.spring.config.Common;
import org.chobit.spring.config.ConfigUnit;
import org.chobit.spring.config.Consumer;
import org.chobit.spring.exception.KafkaConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;
import java.util.Map;

import static org.chobit.spring.StringKit.isBlank;

/**
 * Kafka配置参数管理类
 *
 * @author robin
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties implements InitializingBean {

    /**
     * 一些通用配置
     */
    private Common common;

    /**
     * 节点配置
     */
    private Map<String, ConfigUnit> config;


    public Map<String, ConfigUnit> getConfig() {
        return config;
    }

    public void setConfig(Map<String, ConfigUnit> config) {
        this.config = config;
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public Collection<ConfigUnit> configs() {
        return this.config.values();
    }

    /**
     * 在配置信息注入完成后执行检查
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == this.config || this.config.isEmpty()) {
            throw new KafkaConfigException("Invalid kafka config");
        }
        for (Map.Entry<String, ConfigUnit> ele : this.config.entrySet()) {
            String groupId = ele.getKey();
            ConfigUnit cfg = ele.getValue();
            if (isBlank(cfg.getGroupId())) {
                cfg.setGroupId(groupId);
            }
            check(cfg);
        }
    }

    private void check(ConfigUnit config) {
        if (isBlank(config.getGroupId())) {
            throw new KafkaConfigException("Id of kafka config unit is necessary");
        }
        if (isBlank(config.getBootstrapServers())) {
            throw new KafkaConfigException("Bootstrap servers of kafka config " + config.getGroupId() + " is empty");
        }
        if (null == config.getTopics() || config.getTopics().isEmpty()) {
            throw new KafkaConfigException("Topics of kafka config " + config.getGroupId() + " is empty");
        }
        if (null != config.getConsumer()) {
            Consumer c = config.getConsumer();
            if (isBlank(c.getProcessor())) {
                throw new KafkaConfigException("Processor for consumer group:" + config.getGroupId() + " cannot be null.");
            }
        }
    }
}

