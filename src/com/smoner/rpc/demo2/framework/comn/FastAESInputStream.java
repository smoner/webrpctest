package com.smoner.rpc.demo2.framework.comn;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FastAESInputStream extends InputStream {


    private byte[] p = new byte[16];// 保存解密前数据
    private byte[] p1 = new byte[16];// 保存解密后数据

    private InputStream in;

    private int loc = 16;

    private boolean changMeaning = false;// 字符转义

    private byte[] transKey;

    public FastAESInputStream(InputStream inSet, byte[] transKey) throws IOException {
        super();
        this.transKey = transKey;
        in = inSet;
    }

    public FastAESInputStream(InputStream inSet) {
        super();
        in = inSet;
    }

    public void close() throws IOException {
        in.close();
    }

    public int read() throws java.io.IOException {
        while (true) {
            if (loc == 16) {
                int len = 0;
                while (len != 16) {
                    int readLen = in.read(p, len, 16 - len);
                    if (readLen > 0)
                        len += readLen;
                    if (readLen == -1) {
                        if (len != 0 && len != 16) {
                            throw new EOFException(" The end of the file!");
                            // break;
                        } else
                            return -1;
                    }
                }
                loc = 0;
            }

            if (changMeaning) {
                changMeaning = false;
                if (p1[loc] != NetStreamConstants.ENDEDCODE) {
                    loc = 16;
                    continue;
                }
                break;
            } else if (p1[loc] == NetStreamConstants.ENDEDCODE) {
                changMeaning = true;
                loc++;
                continue;
            } else
                break;
        }
        byte pb = p1[loc++];
        int pi = pb & 0xff;
        return pi;
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        int readCount = 0;
        for (int i = 0; i < len; i++) {
            int v = read();
            if (v != -1) {
                readCount++;
                b[off + i] = (byte) v;
            } else {
                if (readCount == 0)
                    return -1;
                else
                    break;
            }
        }
        return readCount;
    }

}
