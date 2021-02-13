package org.chobit.spring;

/**
 * 关闭
 *
 * @author robin
 */
public interface Shutdown {

    /**
     * 执行shutdown
     *
     * @throws Exception
     */
    void shutdown() throws Exception;

    /**
     * 等待完全关闭
     *
     * @throws Exception
     */
    void awaitShutdown() throws Exception;
}
