package org.chobit.kafka.utils;

/**
 * 线程工具类
 *
 * @author robin
 */
public final class Threads {
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Threads {


    public static ExecutorService newFixCachedThreadPool(int poolSize,
                                                         String threadNamePattern,
                                                         boolean isDaemon) {
        return new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(), newThreadFactory(threadNamePattern, isDaemon), new ThreadPoolExecutor.CallerRunsPolicy());
    }


    public static Thread newThread(Runnable runnable, String threadName, boolean isDaemon) {
        Thread t = new Thread(runnable, threadName);
        t.setDaemon(isDaemon);
        return t;
    }


    private Threads() {
        throw new UnsupportedOperationException("Private constructor, cannot be accessed.");
    }
}
