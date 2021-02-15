package org.chobit.spring;

import org.chobit.spring.config.ConfigUnit;

import java.util.Collection;
import java.util.Map;

/**
 * 以字符串为key和value的生产者访问工具类
 *
 * @author robin
 */
public class StringProducerTemplate extends ProducerTemplate<String, String> {

    /**
     * 构造器
     *
     * @param commonConfig 通用配置
     * @param configs      具体配置集合
     */
    public StringProducerTemplate(Map<String, Object> commonConfig,
                                  Collection<ConfigUnit> configs) {
        super(commonConfig, configs);
    }

}
