package com.smoner.rpc.demo2.framework.comn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FastDESIOStream implements IFastIOStream {

    @Override
    public FastDESOutputStream createOutputStream(OutputStream out,byte[] key)
            throws IOException {
        return new FastDESOutputStream(out);
    }

    @Override
    public FastDESInputStream createInputStream(InputStream in,byte[] key)
            throws IOException {
        return new FastDESInputStream(in);
    }

}