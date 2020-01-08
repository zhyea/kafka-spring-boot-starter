package org.chobit.kafka.autoconfig;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.chobit.kafka.ConsumerStarter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 自启动管理类
 *
 * @author zhangrui137
 */
@Configuration
@ConditionalOnClass({KafkaConsumer.class, KafkaProducer.class})
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {


    private final KafkaProperties properties;

    public KafkaAutoConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnProperty(prefix = "kafka.consumers[0]", name = {"group_id", "topics", "processor"})
    @Bean
    public ConsumerStarter<?, ?> consumerStarter() {
        return new ConsumerStarter<>(properties.getCommon(), properties.getConsumers());
    }


    @ConditionalOnProperty(prefix = "kafka.producer", name = "enable", havingValue = "true")
    @Bean
    public org.chobit.kafka.KafkaProducer<?, ?> producer() {
        return new org.chobit.kafka.KafkaProducer<>(properties.getProducer());
    }

}
