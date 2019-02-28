package org.chobit.kafka.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chobit.kafka.exception.KafkaConfigException;

import java.io.IOException;

public abstract class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();


    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new KafkaConfigException(String.format("Read from json:[%s] error.", json), e);
        }
    }

    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new KafkaConfigException("Read from bytes error.", e);
        }
    }
}
