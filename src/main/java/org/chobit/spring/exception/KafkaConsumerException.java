package org.chobit.spring.exception;

/**
 * 自定义kafka消费异常类
 *
 * @author robin
 */
public class KafkaConsumerException extends RuntimeException {

    public KafkaConsumerException(Throwable t) {
        super("Kafka consume error.", t);
    }


}
