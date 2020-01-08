package org.chobit.kafka;

interface Shutdown {

    void shutdown();

    void awaitShutdown();
}
