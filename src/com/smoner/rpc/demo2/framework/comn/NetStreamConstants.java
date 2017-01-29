package com.smoner.rpc.demo2.framework.comn;

/**
 * Created by smoner on 2017/1/27.
 */
public class NetStreamConstants {

    public static final int NC_STREAM_MAGIC = 0x897172;


    public static final int NC_STREAM_BUFFER_SIZE = 8192;

    public final static byte[] NC_STREAM_HEADER = { (byte) NC_STREAM_MAGIC,
            (byte) (NC_STREAM_MAGIC >> 8), (byte) (NC_STREAM_MAGIC >> 16) };

    /**
     * crypt end code
     */
    public static final byte ENDEDCODE = (byte) 100;

    public static boolean STREAM_NEED_COMPRESS = "true".equals(System
            .getProperty("nc.stream.compress", "true"));

    public static boolean STREAM_NEED_ENCRYPTED = "true".equals(System
            .getProperty("nc.stream.encrypted", "true"));

    public static int STREAM_ENCRYPTED_TYPE = "DES"
            .equalsIgnoreCase(System
                    .getProperty("nc.stream.encryptType", "DES")) ? 0 : ("AES"
            .equalsIgnoreCase(System.getProperty("nc.stream.encryptType")) ? 1
            : 2);

    public static boolean STREAM_AUTO_ADAPT = "true".equals(System
            .getProperty("nc.stream.autoAdapt"));

    public static boolean STREAM_NEED_STATISTIC = "true".equals(System
            .getProperty("nc.stream.statistic"));

    public static class EnctyptType {

        public static final int DES = 0;

        public static final int AES = 1;

        public static final int DynamicAES = 2;
    }
}
