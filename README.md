# 简介

当前产品是一个简化版的spring-boot-starter-kafka组件，只提供了生产和消费两种能力

该组件可以通过添加如下依赖启用：

```xml
    <dependency>
        <groupId>org.chobit.kafka</groupId>
        <artifactId>spring-boot-starter-kafka</artifactId>
        <version>xxx</version>
    </dependency>
```

xxx为版本号。当前版本号为0.1。


这里是一个配置文件示例：

```yaml
kafka:
  common:
    bootstrap_servers: kafka25:9092,kafka26:9092,kafka27:9092
  consumers:
    - group_id: test-group
      topics: [test-topic1,test-topic2]
      processor: org.chobit.spring.component.MyProcessor
      count: 3
  producer:
    enable: false
```

启用生产者需将对应的**producer.enable**选项设置为true。使用时可以直接通过`@Autowired`注解获取`org.chobit.kafka.KafkaProducer`实例。

其中**processor**属性是具体执行消费处理逻辑的实例。它的值可以是类名，也可以是在`@Component`（或其他）注解中设置的name。相关类需要实现`Processor`接口。

设置**consumers[n].count**需要参考Kafka分区的数量，公式大致是：**count * node = partition**。如果要进一步提升消费能力，建议在processor中添加多线程处理。

生产者默认使用common中配置的**bootstrap_servers**的值，如有需要也可在**producer**下配置**bootstrap_servers**信息。

