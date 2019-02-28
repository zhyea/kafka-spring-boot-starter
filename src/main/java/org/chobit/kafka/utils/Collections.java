package org.chobit.kafka.utils;

import java.util.Collection;

public abstract class Collections {

    public static boolean isEmpty(Collection<?> coll) {
        return null == coll || coll.isEmpty();
    }

}
