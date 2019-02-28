package org.chobit.kafka.exception;

public class ReflectFailedException extends RuntimeException {

    public ReflectFailedException(String message) {
        super(message);
    }

    public ReflectFailedException(Throwable t) {
        super(t);
    }
}
