package org.chobit.kafka.exception;

public class KafkaConsumeException extends RuntimeException {

    public KafkaConsumeException(Throwable t) {
        super("Kafka consume error.", t);
    }

    public KafkaConsumeException(String message, Throwable t) {
        super(message, t);
    }


}
