package com.smoner.rpc.demo3.myframework.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpRMIContext extends AbstractRMIContext implements RMIContext {

    private HttpServletRequest request;

    private HttpServletResponse response;


    public HttpRMIContext(HttpServletRequest request,
                          HttpServletResponse response) {
        super();
        this.request = request;
        this.response = response;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public boolean isCompressed() {
        return false;
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public void setCompressed(boolean compressed) {

    }

    @Override
    public void setEncrypted(boolean encrypted) {

    }

    @Override
    public int getEncryptType() {
        return 0;
    }

    @Override
    public void setEncryptType(int type) {

    }
}