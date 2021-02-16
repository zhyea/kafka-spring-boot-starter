package org.chobit.spring;

import org.chobit.spring.config.ConfigUnit;
import org.chobit.spring.config.Consumer;
import org.chobit.spring.exception.KafkaConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 消费者启动类
 *
 * @author robin
 */
public final class ConsumerBeanProcessor<K, V> implements Shutdown, SmartInitializingSingleton, DisposableBean, BeanPostProcessor, ApplicationContextAware, Ordered {


    private final Logger logger = LoggerFactory.getLogger(ConsumerBeanProcessor.class);

    /**
     * 消费线程执行者
     */
    private final List<ConsumerWorker<K, V>> workers;

    /**
     * 消费线程总数
     */
    private final int totalWorkers;

    /**
     * 通用消费配置
     */
    private final Map<String, Object> commonProps;

    /**
     * 配置集合
     */
    private final Collection<ConfigUnit> configs;

    /**
     * 消息处理器集合
     */
    private final Map<String, Processor<K, V>> processors;

    /**
     * 并发计数器
     */
    private final CountDownLatch startupLatch;

    /**
     * 需要使用的Processor实现类名称（类名或Qualifier名称集合）
     */
    private final Set<String> expectProcessors;

    /**
     * Consumer服务启动标识
     */
    private final AtomicBoolean startSignal = new AtomicBoolean(false);

    /**
     * Consumer服务关闭标识
     */
    private final AtomicBoolean shutdownSignal = new AtomicBoolean(false);

    /**
     * 容器初始化标记
     */
    private boolean refreshEventReceived = false;

    /**
     * 构造器
     *
     * @param commonProps 通用属性
     * @param configs     配置信息集合
     */
    public ConsumerBeanProcessor(Map<String, Object> commonProps,
                                 Collection<ConfigUnit> configs) {
        this.commonProps = commonProps;
        this.configs = configs;

        int totalConsumers = configs.size();

        this.totalWorkers = configs.stream().mapToInt(e -> e.getConsumer().getCount()).sum();

        this.workers = new ArrayList<>(totalWorkers);
        this.processors = new ConcurrentHashMap<>(totalConsumers);
        this.expectProcessors = new HashSet<>(4);

        for (ConfigUnit c : configs) {
            expectProcessors.add(c.getConsumer().getProcessor());
        }

        this.startupLatch = new CountDownLatch(totalWorkers);

        System.setProperty("kafka.consumer.enable", "false");
    }


    public void startup() {
        if (totalWorkers <= 0) {
            logger.info("Kafka consumer workers is none.");
            return;
        }

        Threads.newThread(() -> {
            // 等待启动信号，若未接到启动信号则sleep一段时间
            while (!startSignal.get()) {

                if (shutdownSignal.get()) {
                    return;
                }

                if (refreshEventReceived) {
                    logger.error("Cannot find processors: {}, Kafka Clients start failed.", expectProcessors);
                    return;
                }

                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    logger.error("Current thread was interrupted.", e);
                }
            }

            startup0();
        }, "Consumer-starter-auto-start-thread", false).start();
    }


    /**
     * 启动ConsumerWorker实例
     */
    private void startup0() {
        int idx = 0;
        // 构建不同Group的ConsumerWorker实例
        for (ConfigUnit cfg : configs) {
            Consumer consumer = cfg.getConsumer();
            Processor<K, V> p = processors.get(cfg.getConsumer().getProcessor());
            if (null == p) {
                throw new KafkaConfigException("Cannot find any bean for Processor:" + consumer.getProcessor() + " in consumer group:" + cfg.getGroupId());
            }

            // 获取消费配置信息
            Map<String, Object> props = new HashMap<>(8);
            if (null != this.commonProps) {
                props.putAll(commonProps);
            }
            props.putAll(cfg.consumerConfig());

            // 为同一个group创建多个ConsumerWorker实例
            for (int i = 0; i < consumer.getCount(); i++) {
                ConsumerWorker<K, V> worker = new ConsumerWorker<>(props, consumer, p, cfg.getGroupId(), cfg.getTopics(), startupLatch);
                if (shutdownSignal.get()) {
                    startupLatch.countDown();
                } else {
                    workers.add(worker);
                    Threads.newThread(worker, "Consume-worker-thread-" + idx++, false).start();
                }
            }
            // continue
        }


        try {
            startupLatch.await();
        } catch (InterruptedException e) {
            logger.error("Current thread was interrupted.", e);
        }

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
    public void afterSingletonsInstantiated() {
        startup();
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
    public void shutdown() throws InterruptedException {
        shutdownSignal.set(true);

        startupLatch.await();

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
    public void destroy() throws InterruptedException {
        shutdown();
        awaitShutdown();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private void onApplicationEvent(ContextRefreshedEvent event) {
        this.refreshEventReceived = true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).addApplicationListener(new ContextRefreshListener());
        }
    }


    /**
     * ApplicationListener endpoint that receives events from this servlet's WebApplicationContext
     * only, delegating to {@code onApplicationEvent} on the FrameworkServlet instance.
     */
    private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            ConsumerBeanProcessor.this.onApplicationEvent(event);
        }
    }

}
