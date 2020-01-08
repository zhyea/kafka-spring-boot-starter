package org.chobit.kafka;

import org.chobit.kafka.exception.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象消费者线程，主要提供线程安全之类的辅助方法
 *
 * @author robin
 */
public abstract class AbstractConsumerThread implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private CountDownLatch startupLatch = new CountDownLatch(1);

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    public void startupComplete() {
        isRunning.set(true);
        startupLatch.countDown();
    }


    public void awaitStartup() {
        try {
            startupLatch.await();
        } catch (InterruptedException e) {
            throw new KafkaException(e);
        }
    }


    public void shutdownComplete() {
        isRunning.set(false);
        shutdownLatch.countDown();
    }


    public void awaitShutdown() {
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            throw new KafkaException(e);
        }
    }


    public Boolean isRunning() {
        return isRunning.get();
    }
}
