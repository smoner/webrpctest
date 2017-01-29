package com.smoner.rpc.demo2.framework.comn;


public class IOStreamFactory {

    private static FastDESIOStream fastDes;
    private static FastAESIOStream fastAes;
    private static FastDynamicAESIOStream fastDynamicAes;

    public static IFastIOStream getFastIOStream(int type) {
        if (NetStreamConstants.EnctyptType.DynamicAES == type)
            return getFastDynamicAes();
        else if (NetStreamConstants.EnctyptType.AES == type)
            return getFastAes();
        else
            return getFastDes();
    }

    public static byte genHeader(int type) {
        byte header = 0;
        header |= 0x1;
        if (type == NetStreamConstants.EnctyptType.AES)
            header |= 0x4;
        else if (type == NetStreamConstants.EnctyptType.DynamicAES) {
            header |= 0x8;
        }
        return header;
    }

    public static int genEncryptType(int header) {
        int encryptType;
        if ((header & 0x4) == 0 && (header & 0x8) == 0)
            encryptType = NetStreamConstants.EnctyptType.DES;
        else if ((header & 0x8) != 0)
            encryptType = NetStreamConstants.EnctyptType.DynamicAES;
        else
            encryptType = NetStreamConstants.EnctyptType.AES;
        return encryptType;
    }

    private static FastDESIOStream getFastDes() {
        if (fastDes == null) {
            synchronized (IOStreamFactory.class) {
                if (fastDes == null) {
                    fastDes = new FastDESIOStream();
                }
            }
        }
        return fastDes;
    }

    private static FastAESIOStream getFastAes() {
        if (fastAes == null) {
            synchronized (IOStreamFactory.class) {
                if (fastAes == null) {
                    fastAes = new FastAESIOStream();
                }
            }
        }
        return fastAes;
    }

    private static FastDynamicAESIOStream getFastDynamicAes() {
        if (fastDynamicAes == null) {
            synchronized (IOStreamFactory.class) {
                if (fastDynamicAes == null) {
                    fastDynamicAes = new FastDynamicAESIOStream();
                }
            }
        }
        return fastDynamicAes;
    }
}