# 简介

**kafka-spring-boot-starter**是一个SpringBoot环境下的kafka客户端组件。

## 添加依赖

这个组件已经提交到了maven中央仓库，可以直接通过依赖的形式引入：

```xml
	<dependency>
		<groupId>org.chobit.spring</groupId>
		<artifactId>kafka-spring-boot-starter</artifactId>
		<version>[0.2.2,)</version>
	</dependency>
```
**0.2.2**是这两天刚发布的一个版本。

## 消费者`Processor`

**kafka-spring-boot-starter**这个组件已经完成了kafka消费者的主要功能，在使用时只需要关注消息该如何处理。对于开发者来说，只需要实现`Processor`接口并注入到容器中即可。

下面是一个简单的示例：

```java
@Component("zhyyy")
public class MyProcessor implements Processor<String, String> {

    @Override
    public void process(ConsumerRecords<String, String> records) {

        for (ConsumerRecord<String, String> r : records) {
            String json = r.value();
            System.out.println(json);
        }
    }

}
```

如示例中，通过`@Component`注解完成了`Processor`实现类的实例的注入，并为注入的Bean提供了一个名称：**zhyyy**。记住这个名称，在之后的配置文件中会用到。

## 使用生产者

**kafka-spring-boot-starter**会根据配置主动创建生产者。开发使用时可以直接从容器中获取`ProducerTemplate`：

```java
    @Autowired
    private ProducerTemplate<?, ?> producerTemplate;
```

如果写入kafka的消息的key和value的序列化方案采用的都是默认的字符串（反）序列化方案（`StringDeserializer`和`StringSerializer`），可以使用`StringProducerTemplate`实例：

```java
    @Autowired
    private StringProducerTemplate producerTemplate;
```

发送消息时酌情调用不同的`send()`方法：

```java
void send(String topic, V value){...}

void send(String topic, K key, V value){...}

void send(String topic, V value, Callback callback){...}

void send(String topic, K key, V value, Callback callback) {...}
```

## 配置

下面是一个最简单的配置：

```yml
kafka:
  config:
    test-group00:
      bootstrap-servers: kafka1,kafka2,kafka3
      topics: test-topic1
      consumer:
        processor: zhyyy
        count: 4
```

如上配置中：

* **test-group00**既是配置项的ID，也是消费组ID
* **bootstrap-servers**我想不需要多做解释。
* **topics**对应的是一个数组结构，也可以写作`[test-topic1]`或`[test-topic1,test-topic2]`，即支持同一个kafka集群上多个类似topic的统一处理
* **consumer**是消费者相关配置，processor对应的是`Processor`实现类的Bean名称，count标识的是应用内消费线程的数量

默认的序列化方案采用的是字符串序列化方案。怎么做个性化配置稍后会做介绍。

虽然在配置中没有体现，但**kafka-spring-boot-starter**组件会基于现有的信息设置`KafkaProducer`，使用时可以通过`ProducerTemplate`执行消息发送。

比较完整的配置是这样子的：

```yml

kafka:
  common:
    consumer:
      prop1: value1
      prop2: value2
    producer:
      prop3: value3
      prop4: value4
  config:
    test-group00:
      bootstrapServers: kafka1,kafka2,kafka3
      topics: test-topic
      consumer:
        autoOffsetReset: latest
        processor: zhyyy
        count: 4
        keyDeserializer: org.apache.kafka.common.serialization.StringDeserializer
        valueDeserializer: org.apache.kafka.common.serialization.StringDeserializer
        props:
          prop1: value1
          prop2: value2
      producer:
        keySerializer: org.apache.kafka.common.serialization.StringSerializer
        valueSerializer: org.apache.kafka.common.serialization.StringSerializer
        props:
          prop3: value3
          prop4: value4
    test-group02:
      bootstrapServers: kafka4,kafka5,kafka6
      topics: test-topic2
      consumer:
        autoOffsetReset: latest
        processor: zhyyy
```

其中**common**模块下是一些通用的配置，**config**模块下则是一或多组具体的配置。**common**下的配置会被**config**下的配置覆盖。

此外还独立出来了一些常用的配置项，如**autoOffsetReset**，**keyDeserializer**等，以便在使用时配置。
