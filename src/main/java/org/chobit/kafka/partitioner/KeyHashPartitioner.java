package org.chobit.kafka.partitioner;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
import org.apache.kafka.common.KafkaException;

public class KeyHashPartitioner implements Partitioner {


    public KeyHashPartitioner(VerifiableProperties props) {
    }

    @Override
    public int partition(Object key, int numPartitions) {
        int result = 0;

        try {
            if (numPartitions > 0 && key != null && !"".equals(key)) {
                result = Math.abs(key.hashCode()) % numPartitions;
            }
        } catch (Exception e) {
            throw new KafkaException("Compute kafka partition num failed.");
        }

        return result;
    }
}
