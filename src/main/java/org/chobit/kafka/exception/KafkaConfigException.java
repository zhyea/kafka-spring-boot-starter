package org.chobit.kafka.exception;

/**
 * 自定义kafka配置异常类
 *
 * @author robin
 */
public class KafkaConfigException extends RuntimeException {

    public KafkaConfigException(String message) {
        super(message);
    }

}
