package com.smoner.rpc.demo2.framework.pub.lang;


/**
 * 字符串工具类，提供一些字符串相关的操作。
 *
 * <DL>
 * <DT><B>Provider:</B></DT>
 * <DD>NC-UAP</DD>
 * </DL>
 *
 */
public class StringUtil {
    /**
     * 检查字符串是否为空串("")或<tt>null</tt>。不会trim给定字符串。
     *
     * @param str
     *            the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}