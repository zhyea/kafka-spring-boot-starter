package org.chobit.kafka.config;


import org.chobit.kafka.exception.KafkaConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
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


    private List<ConfigUnit> units;

    private ConfigUnit unit;


    public List<ConfigUnit> getUnits() {
        return units;
    }

    public void setUnits(List<ConfigUnit> units) {
        this.units = units;
    }

    public ConfigUnit getUnit() {
        return unit;
    }

    public void setUnit(ConfigUnit unit) {
        this.unit = unit;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == this.units) {
            this.units = new LinkedList<>();
        }
        if (null != unit) {
            this.units.add(this.unit);
        }
        for (ConfigUnit config : this.units) {
            check(config);
        }

    }

    private void check(ConfigUnit config) {
        if(isBlank(config.getId())){
            throw new KafkaConfigException("Id of kafka config unit is necessary");
        }
        if (null == config.getCommon() || isBlank(config.getCommon().getBootstrapServers())) {
            throw new KafkaConfigException("Cannot find valid bootstrap-servers for " + config.getId());
        }
        if (null != config.getConsumers()) {
            for (Consumer c : config.getConsumers()) {
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
