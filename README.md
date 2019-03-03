# 简介

spring-boot-starter-kafka82x是一个支持自动配置的springboot kafka组件。  

工作中使用的springboot版本（1.5.4）不支持kafka0.8.2.x版本，所以才完成了这个组件。

这个组件特点如下：

* 基于springboot1.5.4、kafka0.8.2版本开发
* 实现了springboot自动配置
* 配置灵活，支持多kafka集群

# 引入依赖

这个组件依赖的spring和kafka都是比较老旧的版本，而且使用场景也比较有限，所以就没有上传到maven中心仓库。可以自行pull当前工程并**mvn install**到本地仓库。

在pom中添加依赖如下：

```xml

```

# 配置说明

下面是一个简单的配置示例：

```yml
```

在示例中可以看到，配置主要分成了三个部分：zookeepers、consumers和producers。每个部分都以列表的形式提供，目的是为了支持多kafka集群、多consuemr group、多producer。

此外还有一个配置项**autoStart**，该项配置在值为true的情况下会默认启动配置中的kafka consumer group。

## zookeepers

zookeepers配置的是kafka集群使用的zookeeper信息。每个zookeeper以id进行标识，consumer和producer的配置中也通过id来引用zookeeper。

## consumers

这里配置的是consumer group信息。配置列表中每个元素的id为consumerGroupId。  

processor是当前consumer group消费消息后的处理类。消息处理类需要实现**org.chobit.spring.kafka.Processor**接口。需要提一下的是：在当前组件中，每个processor目前是以单例的形式进行调用，应当避免在Processor接口的实现类中出现共享资源。

每个consumer group可以消费多个topic，topic信息在topics下添加，包括topic的名称和每个实例消费的partition数目。

配置项说明：  

| 配置项 | 默认值 | 说明 |
| ----- | ----- | ----- |
|  |  |  |

此外，前面还提过，顶端的配置项autoStart会在值为true的情况下默认启动这里配置的所有consumer group。若autoStart的值为false，则需要通过**org.chobit.spring.kafka.ConsumerStarter**的一个Bean实例来调用startup()方法进行显式启动。

## producers

kafka 生产者的配置。

配置项说明：  

| 配置项 | 默认值 | 说明 |
| ----- | ----- | ----- |
|  |  |  |

# 其他  

无
