package org.chobit.kafka.config;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.chobit.kafka.ConsumerBeanProcessor;
import org.chobit.kafka.ProducerAgent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;


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
        return new ConsumerBeanProcessor<>(properties.getUnits());
    }


    @ConditionalOnClass(KafkaProducer.class)
    @Bean
    public ProducerAgent producer() {
        return new ProducerAgent(properties.getUnits());
    }

}
