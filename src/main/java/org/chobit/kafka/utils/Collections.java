package org.chobit.kafka.utils;

import java.util.Collection;
import java.util.Map;

public abstract class Collections {

    public static boolean isEmpty(Collection<?> coll) {
        return null == coll || coll.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

}
