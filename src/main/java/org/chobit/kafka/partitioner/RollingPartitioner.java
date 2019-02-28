package org.chobit.kafka.partitioner;


import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RollingPartitioner implements Partitioner {


    private static Map<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();


    public RollingPartitioner(VerifiableProperties props) {
    }

    @Override
    public int partition(Object key, int numPartitions) {
        if (!map.containsKey(key.toString())) {
            map.put(key.toString(), new AtomicInteger(0));
        }
        AtomicInteger curr = map.get(key.toString());
        curr.compareAndSet(numPartitions * 1024, 0);
        return curr.getAndIncrement() % numPartitions;
    }
}