package org.chobit.kafka.config;

/**
 * 在无提交的offset时，consumer消费开始的位置
 *
 * @author robin
 */
public enum AutoOffset {

    /**
     * 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
     */
    latest,
    /**
     * 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
     */
    earliest,
    ;

}
