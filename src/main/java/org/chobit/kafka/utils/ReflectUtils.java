package org.chobit.kafka.utils;

import org.chobit.kafka.exception.ReflectFailedException;

public abstract class ReflectUtils {

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectFailedException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectFailedException(e);
        }
    }

}
