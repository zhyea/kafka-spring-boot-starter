package org.chobit.kafka;

public interface Processor<V> extends Shutdown {


    boolean process(String topic, V message);


}
