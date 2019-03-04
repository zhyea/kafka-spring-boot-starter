# 简介

spring-boot-starter-kafka82x是一个支持自动配置的springboot kafka组件。  

工作中使用的springboot版本（1.5.4）不支持kafka0.8.2.x版本，所以才完成了这个组件。

组件特点如下：

* 基于springboot1.5.4、kafka0.8.2版本开发
* 实现了springboot自动配置
* 配置灵活，支持多kafka集群

# 引入依赖

组件依赖的spring和kafka都是比较老旧的版本，而且使用场景也比较有限，所以就没有上传到maven中心仓库。可以自行pull当前工程并**mvn install**到本地仓库。

在pom中添加依赖如下：

```xml
<dependency>
    <groupId>org.chobit.spring</groupId>
    <artifactId>spring-boot-starter-kafka821</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

# 配置说明

下面是一个简单的配置示例：

```yml
kafka:
  autoStart: true
  zookeepers:
    - id: test-zk
      zkConnect: zookeeper1:2181,zookeeper2:2181,zookeeper3:2181/kafka/product/kafka821
  consumers:
    - groupId: test-consumer
      zookeeper: test-zk
      processor: org.chobit.kbs.kafka.MyProcessor
      topics:
        - name: processor_output_impression
          threadNum: 8
      properties:
        - offsets.commit.max.retries: 5
          consumer.timeout.ms: -1
  producers:
    - id: test-producer
      zookeeper: test-zk
```

在示例中可以看到，配置主要分成了三个部分：zookeepers、consumers和producers。每个部分都以列表的形式提供，目的是为了支持多kafka集群、多consuemr group、多producer。

此外还有一个配置项**autoStart**，该项配置在值为true的情况下会默认启动配置中的kafka consumer group。

## zookeepers

zookeepers配置的是kafka集群使用的zookeeper信息。每个zookeeper以id进行标识，consumer和producer的配置中也通过id来引用zookeeper。

## consumers

这里配置的是consumer group信息。配置列表中每个元素的groupId为consumerGroupId。  

processor是当前consumer group消费消息后的处理类。消息处理类需要实现**org.chobit.spring.kafka.Processor**接口。需要提一下的是：在当前组件中，每个processor目前是以单例的形式进行调用，应当避免在Processor接口的实现类中出现共享资源。

每个consumer group可以消费多个topic，topic信息在topics下添加，包括topic的名称和每个实例消费的partition数目。

配置项说明：  

| 配置项 | 默认值 | 说明 |
| ----- | ----- | ----- |
| groupId | 无 | consumer group id |
| zookeeper | 无 | zookeeperId |
| processor | 无 | 消息处理逻辑实现 |
| keySerializer | org.chobit.kafka.serializer.StringSerializer | message key反序列化实现 |
| serializer | org.chobit.kafka.serializer.StringSerializer | message反序列化实现 |
| topics | 无 | 要消费的topic列表，每个topic配置见下方 |
| properties | 无 | kafka的consumer配置项，[点此查看](http://kafka.apache.org/082/documentation.html#consumerconfigs) |

topic配置

| 配置项 | 默认值 | 说明 |
| ----- | ----- | ----- |
| name | 无 | topic名称 |
| threadNum | 无 | 每个topic分配的消费线程数目，对应该topic的partition数目 |

此外，前面还提过，顶端的配置项autoStart会在值为true的情况下默认启动这里配置的所有consumer group。若autoStart的值为false，则需要通过**org.chobit.spring.kafka.ConsumerStarter**的一个Bean实例来调用startup()方法进行显式启动。

## producers

kafka 生产者配置。

配置项说明：  

| 配置项 | 默认值 | 说明 |
| ----- | ----- | ----- |
| id | 无 | 生产者ID |
| zookeeper | 无 | zookeeperId |
| type | sync | 生产者类型，sync或async |
| keySerializer | org.chobit.kafka.serializer.StringSerializer | message key反序列化实现 |
| serializer | org.chobit.kafka.serializer.StringSerializer | message反序列化实现 |
| partitioner | org.chobit.kafka.partitioner.RollingPartitioner | 发送消息的分区ID获取类，这里默认提供了RollingPartitioner和KeyHashPartitioner |
| properties | 无 | kafka的producer配置项，[点此查看](http://kafka.apache.org/082/documentation.html#producerconfigs) |


# 其他  

写了一个简单的示例工程，放在了csdn。若有兴趣可以下载看一下。[点击此处]()进入下载页。

