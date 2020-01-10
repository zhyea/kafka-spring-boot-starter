package org.chobit.kafka.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadsTest {


    public static void main(String[] args) {
        ExecutorService executorService = Threads.newFixCachedThreadPool(3, "test-thread-%d", false);
        AtomicInteger count = new AtomicInteger(0);
        while (true) {
            executorService.submit(new MyThread(count.getAndIncrement()));
        }
    }

    public static class MyThread implements Runnable {

        private int id;

        public MyThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10);
                System.out.println(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
