package org.chobit.spring.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产者配置
 *
 * @author robin
 */
public class Producer {

    /**
     * 生产者配置信息
     */
    private Map<String, Object> props = new HashMap<>(8);

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
