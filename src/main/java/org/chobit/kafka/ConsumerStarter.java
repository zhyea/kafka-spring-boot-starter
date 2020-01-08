package org.chobit.kafka;

import org.chobit.kafka.autoconfig.Consumer;
import org.chobit.kafka.autoconfig.Properties;
import org.chobit.kafka.exception.KafkaConfigException;
import org.chobit.kafka.utils.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.chobit.kafka.utils.Strings.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 消费者启动类
 *
 * @author robin
 */
public final class ConsumerStarter<K, V> implements Shutdown, InitializingBean, DisposableBean, BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(ConsumerStarter.class);

    private final List<ConsumerWorker<K, V>> workers;

    private final Map<String, Processor<K, V>> processors;

    private final List<Consumer> consumers;

    private final Properties commonConfig;

    /**
     * 需要使用的Processor实现类名称（类名或Qualifier名称集合）
     */
    private final Set<String> expectProcessors;
    private final AtomicBoolean startSignal = new AtomicBoolean(false);

    public ConsumerStarter(Properties properties, List<Consumer> consumers) {
        if (isBlank(properties.getBootstrapServers())) {
            throw new KafkaConfigException("Cannot find valid bootStrapServers");
        }
        if (isEmpty(consumers)) {
            throw new KafkaConfigException("Consumer group is empty, please set [kafka.consumer.enable] to false.");
        }

        this.workers = new ArrayList<>(consumers.size());
        this.processors = new ConcurrentHashMap<>(consumers.size());
        this.expectProcessors = new HashSet<>(4);

        this.commonConfig = properties;
        this.consumers = consumers;

        for (Consumer c : consumers) {
            expectProcessors.add(c.getProcessor());
        }

        System.setProperty("kafka.consumer.enable", "false");
    }


    /**
     * 在实例创建完成后就尝试启动
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Threads.newThread(new Runnable() {
            @Override
            public void run() {
                // 等待启动信号，若未接到启动信号则sleep一段时间
                while (!startSignal.get()) {
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException e) {
                    }
                }

                startup();
            }
        }, "Consumer-starter-auto-start-thread", false).start();
    }


    /**
     * 启动ConsumerWorker实例
     */
    public void startup() {
        // 构建不同Group的ConsumerWorker实例
        for (Consumer c : consumers) {
            Processor<K, V> p = processors.get(c.getProcessor());
            if (null == p) {
                throw new KafkaConfigException("Cannot find any bean for Processor:" + c.getProcessor() + " in consumer group:" + c.getGroupId());
            }
            // 为同一个group创建多个ConsumerWorker实例
            for (int i = 0; i < c.getCount(); i++) {
                workers.add(new ConsumerWorker<>(commonConfig.toMap(), c, p));
            }
        }
        // 启动消费线程
        int i = 0;
        for (ConsumerWorker<K, V> worker : workers) {
            Threads.newThread(worker, "Consume-worker-thread-" + i++, false).start();
            worker.awaitStartup();
        }
        startSignal.set(false);
        logger.info("All consumer groups have been started.");
    }


    /**
     * 获取Processor实例
     * <p>
     * 若已获取到全部Processor实例则设置启动信号为true
     */
    private void addProcessor(String beanName, Object bean) {
        if (expectProcessors.contains(beanName)) {
            processors.put(beanName, (Processor) bean);
            expectProcessors.remove(beanName);
        } else if (expectProcessors.contains(bean.getClass().getName())) {
            processors.put(bean.getClass().getName(), (Processor) bean);
            expectProcessors.remove(bean.getClass().getName());
        }
        if (isEmpty(expectProcessors)) {
            startSignal.set(true);
        }
    }


    @Override
    public void awaitShutdown() {
        if (null != workers) {
            for (ConsumerWorker<K, V> worker : workers) {
                worker.awaitShutdown();
            }
        }
    }


    @Override
    public void shutdown() {
        startSignal.set(false);

        if (null != workers) {
            for (ConsumerWorker<K, V> worker : workers) {
                worker.shutdown();
            }
        }
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    /**
     * 此处会在bean注入完成后，判断这个bean是否是需要的Processor实例
     *
     * @param bean     注入完成的bean
     * @param beanName bean名称
     * @return bean实例
     * @throws BeansException 出错时会抛出
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Processor) {
            addProcessor(beanName, bean);
        }
        return bean;
    }


    /**
     * 在销毁前调用，执行必要的清理工作
     */
    @Override
    public void destroy() {
        awaitShutdown();
        shutdown();
    }
}
