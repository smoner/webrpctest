package com.smoner.rpc.demo2.framework.comn;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FastDynamicAESIOStream implements IFastIOStream {

    @Override
    public FastAESOutputStream createOutputStream(OutputStream out, byte[] key) throws IOException {
        byte[] transKey = { key[0] };
        out.write(transKey);
        out.flush();
        return new FastAESOutputStream(out, transKey);
    }

    @Override
    public FastAESInputStream createInputStream(InputStream in, byte[] key) throws IOException {
        in.read(key);
        return new FastAESInputStream(in, key);
    }
}
