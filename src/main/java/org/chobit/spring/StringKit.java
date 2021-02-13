package org.chobit.spring;

/**
 * 字符串操作工具类
 *
 * @author robin
 */
final class StringKit {

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 字符串是否为空
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    private StringKit() {
        throw new UnsupportedOperationException("Private constructor, cannot be accessed.");
    }
}
