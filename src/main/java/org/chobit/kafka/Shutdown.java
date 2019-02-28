package org.chobit.kafka;

public interface Shutdown {

    void shutdown();

    void awaitShutdown();
}
