package org.chobit.kafka;

/**
 * 线程工具类
 *
 * @author robin
 */
final class Threads {

    public static Thread newThread(Runnable runnable, String threadName, boolean isDaemon) {
        Thread t = new Thread(runnable, threadName);
        t.setDaemon(isDaemon);
        return t;
    }


    private Threads() {
        throw new UnsupportedOperationException("Private constructor, cannot be accessed.");
    }
}
