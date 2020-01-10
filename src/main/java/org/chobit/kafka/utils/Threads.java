package org.chobit.kafka.utils;

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


    public static ThreadFactory newThreadFactory(final String threadNamePattern, final boolean isDaemon) {
        return new ThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            private final ThreadGroup group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r, String.format(threadNamePattern, threadNumber.get()));
                t.setDaemon(isDaemon);
                return t;
            }
        };
    }

}
