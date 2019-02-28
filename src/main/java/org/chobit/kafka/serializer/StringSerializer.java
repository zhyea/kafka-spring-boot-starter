package org.chobit.kafka.serializer;

import kafka.serializer.Decoder;
import kafka.serializer.Encoder;

import java.nio.charset.Charset;

public class StringSerializer implements Decoder<String>, Encoder<String> {

    private static final Charset charset = Charset.forName("UTF-8");
    
    @Override
    public String fromBytes(byte[] bytes) {
        return new String(bytes, charset);
    }

    @Override
    public byte[] toBytes(String s) {
        if (null == s) {
            return null;
        }
        return s.getBytes(charset);
    }
}

