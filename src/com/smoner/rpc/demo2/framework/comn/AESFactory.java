package com.smoner.rpc.demo2.framework.comn;

/**
 * Created by smoner on 2017/1/29.
 */
public class AESFactory {

    // private static final int AES_MAP_MAX_SIZE = 128;

    private static final int AES_KEY_LENGTH = 16;

    private static final int LONG_TO_BYTE = 8;

    private static final int BYTE_LENGTH = 8;

    private static final long SUB_KEY_INFO = 0xDA425C9AB37FEB6CL;

    private static int aesKeyLength = AES_KEY_LENGTH;

    public static final byte[] DEFAULT_KEY = genAesKey(0);
    private static String ClientIP = "";
    private static byte[] transKey = { -1 };


    private static byte[] genAesKey(int index) {
        int length = AESFactory.aesKeyLength;
        byte[] key = new byte[length];
        int count = length / AESFactory.LONG_TO_BYTE;
        byte[] tempBytes = new byte[BYTE_LENGTH];
        long tempLong = 0L;
        for (int i = 0; i < count; i++) {
            tempLong = (AESFactory.SUB_KEY_INFO + index) >>> ((i + 1) * 2);
            tempBytes = long2bytes(tempLong);
            System.arraycopy(tempBytes, 0, key, i * BYTE_LENGTH, BYTE_LENGTH);
        }
        return key;
    }

    /**
     *
     * 将long变成一个数组
     */
    private static byte[] long2bytes(long sd) {
        byte[] dd = new byte[8];
        for (int i = 7; i >= 0; i--) {
            dd[i] = (byte) sd;
            sd >>>= 8;
        }
        return dd;
    }

    public static int getAesKeyLength() {
        return aesKeyLength;
    }

    public static void setAesKeyLength(int aesKeyLength) {
        AESFactory.aesKeyLength = aesKeyLength;
    }

}