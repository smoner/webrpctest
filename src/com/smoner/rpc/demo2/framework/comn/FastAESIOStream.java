package com.smoner.rpc.demo2.framework.comn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FastAESIOStream implements IFastIOStream {

    @Override
    public FastAESOutputStream createOutputStream(OutputStream out, byte[] key) throws IOException {
        return new FastAESOutputStream(out, AESFactory.DEFAULT_KEY);
    }

    @Override
    public FastAESInputStream createInputStream(InputStream in, byte[] key) throws IOException {
        return new FastAESInputStream(in, AESFactory.DEFAULT_KEY);
    }

}