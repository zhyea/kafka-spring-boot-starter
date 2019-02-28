package org.chobit.kafka;

import org.chobit.kafka.autoconfig.ConsumerConfigWrapper;
import org.chobit.kafka.exception.KafkaConfigException;
import org.chobit.kafka.utils.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerBeanProcessor implements Shutdown, BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(ConsumerBeanProcessor.class);

    private final List<ConsumerWorker> workers;
    private final Map<String, Processor> processors;
    private final List<ConsumerConfigWrapper> configs;

    public ConsumerBeanProcessor(List<ConsumerConfigWrapper> configs) {
        this.workers = new ArrayList<ConsumerWorker>(configs.size());
        this.configs = configs;
        this.processors = new ConcurrentHashMap<String, Processor>(configs.size());
    }

    public void startup() {
        for (ConsumerConfigWrapper ccw : configs) {
            Processor p = processors.get(ccw.getProcessor().getName());
            if (null == p) {
                throw new KafkaConfigException("Cannot find bean for Processor class:" + ccw.getProcessor().getName() + " in consumer group:" + ccw.getGroupId());
            }
            workers.add(new ConsumerWorker(ccw, p));
        }
        int i = 0;
        for (ConsumerWorker worker : workers) {
            Threads.newThread(worker, "Consume-worker-thread-" + i++, false).start();
            worker.awaitStartup();
        }

        logger.info("All consumer groups has been started.");
    }


    @Override
    public void shutdown() {
        if (null != workers) {
            for (ConsumerWorker worker : workers) {
                worker.shutdown();
            }
        }

    }

    @Override
    public void awaitShutdown() {
        if (null != workers) {
            for (ConsumerWorker worker : workers) {
                worker.awaitShutdown();
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Processor) {
            processors.put(bean.getClass().getName(), (Processor) bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
