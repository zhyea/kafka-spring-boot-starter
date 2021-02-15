package org.chobit.spring;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.chobit.spring.config.ConfigUnit;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;


/**
 * 自启动管理类
 *
 * @author zhangrui137
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@ConditionalOnClass({KafkaConsumer.class, KafkaProducer.class})
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfiguration {


    private final KafkaProperties properties;


    public KafkaConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnClass(KafkaConsumer.class)
    @Bean
    public ConsumerBeanProcessor<?, ?> consumerProcessor() {
        Map<String, Object> cfg = null;
        if (null != properties.getCommon()) {
            cfg = properties.getCommon().getConsumer();
        }

        Collection<ConfigUnit> coll = properties.configs().stream()
                .filter(e -> null != e.getConsumer())
                .collect(Collectors.toSet());
        return new ConsumerBeanProcessor<>(cfg, coll);
    }


    @ConditionalOnClass(KafkaProducer.class)
    @Bean
    public ProducerTemplate<Object, Object> producer() {
        Map<String, Object> pCfg = null;
        if (null != properties.getCommon()) {
            pCfg = properties.getCommon().getProducer();
        }
        return new ProducerTemplate<>(pCfg, properties.configs());
    }


    @ConditionalOnClass(KafkaProducer.class)
    @Bean
    public StringProducerTemplate stringProducer() {
        Map<String, Object> pCfg = null;
        if (null != properties.getCommon()) {
            pCfg = properties.getCommon().getProducer();
        }

        Collection<ConfigUnit> coll = properties.configs().stream()
                .filter(e -> {
                    Object keyS = e.producerConfig().get(KEY_SERIALIZER_CLASS_CONFIG);
                    Object valS = e.producerConfig().get(VALUE_SERIALIZER_CLASS_CONFIG);
                    return keyS.equals(StringSerializer.class) && valS.equals(StringSerializer.class);
                })
                .collect(Collectors.toSet());

        return new StringProducerTemplate(pCfg, coll);
    }

}
