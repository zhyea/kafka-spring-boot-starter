package org.chobit.kafka.utils;

import java.util.UUID;

public abstract class StringUtils {


    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
