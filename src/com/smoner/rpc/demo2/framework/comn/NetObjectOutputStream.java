package com.smoner.rpc.demo2.framework.comn;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

public class NetObjectOutputStream extends ObjectOutputStream {

    private NCOutputStream ncOut;

    protected ObjectOutputStream objOut;

    private boolean needCompress =false , needEncryp =false;

    private int encryptType;

    private byte[] transKey = { -1 };

    private int bufferSize;

    private boolean finished;

    private CountOutputStream statisticStream;

    public NetObjectOutputStream(OutputStream out) throws IOException {
        this(out, NetStreamConstants.STREAM_NEED_COMPRESS,
                NetStreamConstants.STREAM_NEED_ENCRYPTED,
                NetStreamConstants.NC_STREAM_BUFFER_SIZE);
    }

    public NetObjectOutputStream(OutputStream out, byte[] key)
            throws IOException {
        this(out, NetStreamConstants.STREAM_NEED_COMPRESS, true,
                NetStreamConstants.EnctyptType.DynamicAES, key);
    }

    public NetObjectOutputStream(OutputStream out, int encryptType)
            throws IOException {
        this(out,false, encryptType);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 byte[] key) throws IOException {
        this(out, needCompress, true,
                NetStreamConstants.EnctyptType.DynamicAES, key);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress)
            throws IOException {
        this(out, needCompress, NetStreamConstants.STREAM_NEED_ENCRYPTED,
                NetStreamConstants.STREAM_ENCRYPTED_TYPE,
                NetStreamConstants.NC_STREAM_BUFFER_SIZE);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp) throws IOException {
        this(out, needCompress, needEncryp,
                NetStreamConstants.STREAM_ENCRYPTED_TYPE,
                NetStreamConstants.NC_STREAM_BUFFER_SIZE);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 int encryptType) throws IOException {
        this(out, needCompress, true, encryptType,
                NetStreamConstants.NC_STREAM_BUFFER_SIZE);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp, int bufferSize) throws IOException {
        this(out, needCompress, needEncryp,
                NetStreamConstants.STREAM_ENCRYPTED_TYPE, bufferSize,
                new NCObjectReplacer());
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp, byte[] key) throws IOException {
        this(out, needCompress, needEncryp,
                NetStreamConstants.EnctyptType.DynamicAES, key);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp, int encryptType, int bufferSize)
            throws IOException {
        this(out, needCompress, needEncryp, encryptType, bufferSize,
                new NCObjectReplacer());
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp, int encryptType, byte[] key) throws IOException {
        this(out, needCompress, needEncryp, encryptType,
                NetStreamConstants.NC_STREAM_BUFFER_SIZE,
                new NCObjectReplacer(), key);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp, int encryptType, int bufferSize,
                                 ObjectReplacer replacer) throws IOException {
        super();
        init(out, needCompress, needEncryp, encryptType, bufferSize, replacer);
    }

    public NetObjectOutputStream(OutputStream out, boolean needCompress,
                                 boolean needEncryp, int encryptType, int bufferSize,
                                 ObjectReplacer replacer, byte[] key) throws IOException {
        super();
        setTransKey(key);
        init(out, needCompress, needEncryp, encryptType, bufferSize, replacer);
    }

    public void init(OutputStream out, boolean needCompress,
                     boolean needEncryp, int encryptType, int bufferSize,
                     ObjectReplacer replacer) throws IOException {

        this.needCompress = needCompress;
        this.needEncryp = needEncryp;
        this.encryptType = encryptType & 0x0003;
        this.bufferSize = bufferSize;

        ncOut = new NCOutputStream(out);
        objOut = new NCObjectOuputStream(ncOut, replacer);

        byte[] token = NetStreamContext.getToken();

        if (token != null) {
            objOut.writeInt(token.length);
            objOut.write(token);
        } else {
            objOut.writeInt(0);
        }
    }

    private static class NCObjectOuputStream extends ObjectOutputStream {

        ObjectReplacer replacer;

        public NCObjectOuputStream(OutputStream out, ObjectReplacer replacer)
                throws IOException {
            super(out);
            this.replacer = replacer;
            if (this.replacer != null) {
                enableReplaceObject(true);
            }
        }

        protected Object replaceObject(Object obj) throws IOException {
            if (replacer == null) {
                return obj;
            } else {
                return replacer.replaceObject(obj);
            }
        }

    }

    public void close() throws IOException {
        try {
            finish();
        } catch (Exception exp) {
        }
        if (objOut != null)
            objOut.close();
        objOut = null;
        ncOut = null;
    }

    /**
     * must call if compuress
     *
     * @throws IOException
     */
    public void finish() throws IOException {
        if (!finished) {
            objOut.flush();
            ncOut.finish();
            finished = true;
        }
    }

    public void defaultWriteObject() throws IOException {
        objOut.defaultWriteObject();
    }

    public void flush() throws IOException {
        objOut.flush();
    }

    public PutField putFields() throws IOException {
        return objOut.putFields();
    }

    public void reset() throws IOException {
        objOut.reset();
    }

    public void useProtocolVersion(int version) throws IOException {
        objOut.useProtocolVersion(version);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        objOut.write(buf, off, len);
    }

    public void write(byte[] buf) throws IOException {
        objOut.write(buf);
    }

    public void write(int val) throws IOException {
        objOut.write(val);
    }

    public void writeBoolean(boolean val) throws IOException {
        objOut.writeBoolean(val);
    }

    public void writeByte(int val) throws IOException {
        objOut.writeByte(val);
    }

    public void writeBytes(String str) throws IOException {
        objOut.writeBytes(str);
    }

    public void writeChar(int val) throws IOException {
        objOut.writeChar(val);
    }

    public void writeChars(String str) throws IOException {
        objOut.writeChars(str);
    }

    public void writeDouble(double val) throws IOException {
        objOut.writeDouble(val);
    }

    public void writeFields() throws IOException {
        objOut.writeFields();
    }

    public void writeFloat(float val) throws IOException {
        objOut.writeFloat(val);
    }

    public void writeInt(int val) throws IOException {
        objOut.writeInt(val);
    }

    public void writeLong(long val) throws IOException {
        objOut.writeLong(val);
    }

    public void writeShort(int val) throws IOException {
        objOut.writeShort(val);
    }

    public void writeUnshared(Object obj) throws IOException {
        objOut.writeUnshared(obj);
    }

    public void writeUTF(String str) throws IOException {
        objOut.writeUTF(str);
    }

    protected void writeObjectOverride(Object obj) throws IOException {
        objOut.writeObject(obj);
    }

    public long getNetBytes() {
        return statisticStream == null ? -1 : statisticStream.getCount();
    }

    private class NCOutputStream extends OutputStream {
        private OutputStream out;

        private byte header;

        private DeflaterOutputStream gzipOut;

        private FastDESOutputStream desout;

        private FastAESOutputStream aesout;

        private IFastIOStream ioStream;

        public NCOutputStream(OutputStream output) throws IOException {

            out = new BufferedOutputStream(output, bufferSize);
            if (NetStreamConstants.STREAM_NEED_STATISTIC)
                out = statisticStream = new CountOutputStream(out);

            if (needEncryp) {
                header = IOStreamFactory.genHeader(encryptType);
            }

            if (needCompress)
                header |= 0x2;

            out.write(NetStreamConstants.NC_STREAM_HEADER);
            out.write(header);

            if (needEncryp) {
                ioStream = IOStreamFactory.getFastIOStream(encryptType);
                if (encryptType != NetStreamConstants.EnctyptType.AES
                        && encryptType != NetStreamConstants.EnctyptType.DynamicAES)
                    out = desout = (FastDESOutputStream) ioStream
                            .createOutputStream(out, getTransKey());
                else
                    out = aesout = (FastAESOutputStream) ioStream
                            .createOutputStream(out, getTransKey());
            }

            if (needCompress) {
                out = gzipOut = new FastDeflaterOutputStream(out, bufferSize);
                out = new BufferedOutputStream(out, bufferSize);
            }

        }

        public void write(int b) throws IOException {
            out.write(b);
        }

        public void write(byte[] b, int offset, int len) throws IOException {
            out.write(b, offset, len);
        }

        public void flush() throws IOException {
            out.flush();
        }

        public void finish() throws IOException {
            flush();

            if (gzipOut != null) {
                gzipOut.finish();
                gzipOut.flush();
            }
            if (aesout != null) {
                aesout.finish();
                aesout.flush();
            }
            if (desout != null) {
                desout.finish();
                desout.flush();
            }
        }

        public void close() throws IOException {
            try {
                finish();
            } catch (IOException ioe) {

            }
            out.close();
            if (gzipOut != null)
                gzipOut.close();
            if (aesout != null) {
//				aesout.close();
                aesout = null;
            }
            if (desout != null) {
//				desout.close();
                desout = null;
            }
            out = null;
            gzipOut = null;
            // if (ioStream != null)
            // ioStream.outClose();
            // ioStream = null;
        }

    }

    public static void writeObject(OutputStream output, Object obj)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        NetObjectOutputStream objOut = new NetObjectOutputStream(bout);
        objOut.writeObject(obj);
        objOut.finish();
        objOut.flush();
        writeInt(output, bout.size());
        bout.writeTo(output);
        output.flush();
    }

    public static ByteArrayOutputStream convertObjectToBytes(Object obj,
                                                             boolean compressed, boolean encrypted) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        NetObjectOutputStream objOut = new NetObjectOutputStream(bout,
                compressed, encrypted);
        objOut.writeObject(obj);
        objOut.finish();
        objOut.flush();
        return bout;
    }

    public static void writeInt(OutputStream output, int v) throws IOException {
        byte bytes[] = new byte[4];

        bytes[0] = (byte) ((v >>> 24) & 0xFF);
        bytes[1] = (byte) ((v >>> 16) & 0xFF);
        bytes[2] = (byte) ((v >>> 8) & 0xFF);
        bytes[3] = (byte) ((v >>> 0) & 0xFF);

        output.write(bytes);
    }

    public byte[] getTransKey() {
        return transKey;
    }

    public void setTransKey(byte[] transKey) {
        this.transKey = transKey;
    }

}