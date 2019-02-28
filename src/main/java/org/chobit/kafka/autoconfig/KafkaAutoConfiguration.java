package org.chobit.kafka.autoconfig;


import kafka.consumer.ConsumerConfig;
import kafka.producer.ProducerConfig;
import org.I0Itec.zkclient.ZkClient;
import org.chobit.kafka.ConsumerBeanProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.chobit.kafka.utils.Collections.isEmpty;

@Configuration
@ConditionalOnClass({ProducerConfig.class, ConsumerConfig.class, ZkClient.class})
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public KafkaConfig kafkaConfig(KafkaProperties props) {
        return new KafkaConfigBuilder().build(props);
    }

    @Bean
    public ConsumerBeanProcessor consumerStarter(KafkaConfig kafkaConfig) {
        if (isEmpty(kafkaConfig.getConsumers())) {
            return null;
        }
        return new ConsumerBeanProcessor(kafkaConfig.getConsumers());
    }

}
