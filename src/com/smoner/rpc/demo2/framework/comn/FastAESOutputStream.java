package com.smoner.rpc.demo2.framework.comn;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by smoner on 2017/1/29.
 */
public class FastAESOutputStream extends OutputStream {

    private OutputStream out;

    private byte p[] = new byte[16];// 保存加密前数据

    private byte p1[] = new byte[16];// 保存加密后数据

    private int loc = 0;

    private boolean finished = false;

    private byte[] transKey;

    public FastAESOutputStream(OutputStream outStm, byte[] transKey) throws IOException {
        out = outStm;
    }

    // public FastAESOutputStream(OutputStream outSet, AES aesSet) {
    // aes = aesSet;
    // out = outSet;
    // }

    public void close() throws IOException {
        finish();
        out.close();
    }

    public void flush() throws IOException {
        if (loc == 16) {
            writeOUT();
        }
        out.flush();
    }

    public void finish() throws IOException {
        if (!finished) {
            if (loc < 16) {
                p[loc++] = NetStreamConstants.ENDEDCODE;
                while (loc < 16) {
                    p[loc++] = 0;
                }
            }

            flush();
            finished = true;
        }
    }

    public void write(int b) throws java.io.IOException {
        checkFinished();
        if (loc == 16) {
            writeOUT();
        }

        if (b == NetStreamConstants.ENDEDCODE) {
            p[loc++] = (byte) (b & 0xff);
            if (loc == 16)
                writeOUT();
            p[loc++] = (byte) (b & 0xff);
        } else {
            p[loc++] = (byte) (b & 0xff);
        }

    }

    private void writeOUT() throws IOException {
        out.write(p1);
        loc = 0;
    }

    public boolean finished() {
        return finished;
    }

    private void checkFinished() throws IOException {
        if (finished)
            throw new IOException("AES Output finished");

    }

    public void write(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

}
