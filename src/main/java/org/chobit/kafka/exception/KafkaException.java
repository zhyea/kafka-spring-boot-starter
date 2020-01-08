package org.chobit.kafka.exception;

/**
 * 自定义Kafka异常类
 *
 * @author robin
 */
public class KafkaException extends RuntimeException {

    public KafkaException(Throwable t) {
        super(t);
    }
}
