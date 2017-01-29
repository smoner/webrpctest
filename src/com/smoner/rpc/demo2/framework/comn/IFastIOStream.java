package com.smoner.rpc.demo2.framework.comn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IFastIOStream {

    public OutputStream createOutputStream(OutputStream out, byte[] key)
            throws IOException;

    public InputStream createInputStream(InputStream in, byte[] key)
            throws IOException;
}
