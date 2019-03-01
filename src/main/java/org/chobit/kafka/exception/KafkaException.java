package org.chobit.kafka.exception;

public class KafkaException extends RuntimeException {

    public KafkaException(Throwable t) {
        super(t);
    }

    public KafkaException(String message) {
        super(message);
    }
}
