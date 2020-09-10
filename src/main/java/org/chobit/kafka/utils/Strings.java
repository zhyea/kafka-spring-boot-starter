package org.chobit.kafka.utils;

/**
 * 字符串操作工具类
 *
 * @author robin
 */
public final class Strings {

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

    private Strings() {
        throw new UnsupportedOperationException("Private constructor, cannot be accessed.");
    }
}
