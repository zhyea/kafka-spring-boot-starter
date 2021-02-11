package org.chobit.kafka;

public interface Shutdown {

    void shutdown() throws Exception;

    void awaitShutdown() throws Exception;
}
