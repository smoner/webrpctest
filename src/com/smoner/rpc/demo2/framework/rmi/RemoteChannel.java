package com.smoner.rpc.demo2.framework.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by smoner on 2017/1/29.
 */
public interface RemoteChannel {

    public void init() throws IOException;

    public void destroy();

    public InputStream getInputStream() throws IOException;

    public OutputStream getOutputStream() throws IOException;

    public void processIOException(IOException ioe) throws IOException;

}
