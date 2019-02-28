package org.chobit.kafka.autoconfig;


import kafka.consumer.ConsumerConfig;
import kafka.producer.ProducerConfig;
import org.I0Itec.zkclient.ZkClient;
import org.chobit.kafka.ConsumerStarter;
import org.chobit.kafka.KafkaProducerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.chobit.kafka.utils.Collections.isEmpty;

@Configuration
@ConditionalOnClass({ProducerConfig.class, ConsumerConfig.class})
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {


    @Bean
    public KafkaConfig kafkaConfig(KafkaProperties props) {
        return new KafkaConfigBuilder().build(props);
    }

    @Bean
    public ConsumerStarter consumerStarter(KafkaConfig kafkaConfig) {
        if (isEmpty(kafkaConfig.getConsumers())) {
            return null;
        }
        return new ConsumerStarter(kafkaConfig.isAutoStart(), kafkaConfig.getConsumers());
    }

    @Bean
    @ConditionalOnClass({ProducerConfig.class, ZkClient.class})
    public KafkaProducerFactory producerFactory(KafkaConfig kafkaConfig) {
        if (isEmpty(kafkaConfig.getProducers())) {
            return null;
        }
        return new KafkaProducerFactory(kafkaConfig.getProducers());
    }

}
