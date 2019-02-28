package org.chobit.kafka.exception;

public class KafkaConfigException extends RuntimeException {

    public KafkaConfigException(String message) {
        super(message);
    }

    public KafkaConfigException(String message, Throwable t) {
        super(message, t);
    }

    public KafkaConfigException(Throwable t) {
        this("Kafka config error.", t);
    }

    public KafkaConfigException() {
        this("Kafka config error.");
    }

}
