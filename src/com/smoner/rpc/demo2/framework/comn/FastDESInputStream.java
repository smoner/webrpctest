package com.smoner.rpc.demo2.framework.comn;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by smoner on 2017/1/29.
 */
public class FastDESInputStream extends InputStream {


    private byte[] p = new byte[8];

    private InputStream in;

    private int loc = 8;

    private boolean changMeaning = false;

    public FastDESInputStream(InputStream inSet) {
        super();
        in = inSet;
    }

    public void close() throws IOException {
        in.close();
    }

    public int read() throws java.io.IOException {
        while (true) {
            if (loc == 8) {
                int len = 0;
                while (len != 8) {
                    int readLen = in.read(p, len, 8 - len);
                    if (readLen > 0)
                        len += readLen;
                    if (readLen == -1) {
                        if (len != 0)
                            throw new EOFException(" The end of the file! ");
                        else
                            return -1;
                    }
                }
                loc = 0;
            }

            if (changMeaning) {
                changMeaning = false;
                if (p[loc] != NetStreamConstants.ENDEDCODE) {
                    loc = 8;
                    continue;
                }
                break;
            } else if (p[loc] == NetStreamConstants.ENDEDCODE) {
                changMeaning = true;
                loc++;
                continue;
            } else
                break;
        }
        byte pb = p[loc++];
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