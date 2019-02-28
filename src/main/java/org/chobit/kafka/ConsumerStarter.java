package org.chobit.kafka;

import org.chobit.kafka.autoconfig.ConsumerConfigWrapper;
import org.chobit.kafka.exception.KafkaConfigException;
import org.chobit.kafka.utils.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerStarter implements Shutdown, BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(ConsumerStarter.class);

    private final boolean autoStart;
    private final List<ConsumerWorker> workers;
    private final Map<String, Processor> processors;
    private final List<ConsumerConfigWrapper> configs;

    private final Set<String> expectProcessors;
    private final AtomicBoolean needStart = new AtomicBoolean(true);

    public ConsumerStarter(boolean autoStart, List<ConsumerConfigWrapper> configs) {
        this.autoStart = autoStart;
        this.workers = new ArrayList<ConsumerWorker>(configs.size());
        this.configs = configs;
        this.processors = new ConcurrentHashMap<String, Processor>(configs.size());
        this.expectProcessors = new HashSet<String>();
        for (ConsumerConfigWrapper ccw : configs) {
            expectProcessors.add(ccw.getProcessor().getName());
        }
    }


    @PostConstruct
    public void autoStart() {
        if (autoStart) {
            Threads.newThread(new Runnable() {
                @Override
                public void run() {
                    while (needStart.get()) {
                        if (!checkAllNecessaryProcessorsSupplied()) {
                            continue;
                        }
                        startup();
                    }
                }
            }, "Consumer-starter-auto-start-thread", false).start();
        }
    }


    private boolean checkAllNecessaryProcessorsSupplied() {
        if (processors.size() < expectProcessors.size()) {
            return false;
        }
        for (String s : expectProcessors) {
            if (!processors.containsKey(s)) {
                return false;
            }
        }
        return true;
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
        needStart.set(false);
        logger.info("All consumer groups have been started.");
    }


    @Override
    public void shutdown() {
        needStart.set(false);
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
