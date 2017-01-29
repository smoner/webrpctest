package com.smoner.rpc.demo3.myframework.server;

/**
 * Created by smoner on 2017/1/30.
 */

import com.smoner.rpc.demo3.myframework.client.Result;
import com.smoner.rpc.demo3.myframework.pub.InvocationInfo;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRMIContext implements RMIContext {
    private Map<String, Object> map;
    private InvocationInfo invInfo;
    private Result result;
    private byte[] transKey = new byte[0];

    public byte[] getTransKey() {
        return transKey;
    }

    public void setTransKey(byte[] transKey) {
        this.transKey = transKey;
    }

    public AbstractRMIContext() {
        map = new HashMap<String, Object>();
    }

    @Override
    public void setAttribute(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return map.get(key);
    }

    @Override
    public void clearAttribute() {
        map.clear();
    }

    @Override
    public void removeAttribute(String key) {
        map.remove(key);
    }

    @Override
    public InvocationInfo getInvocationInfo() {
        return invInfo;
    }

    @Override
    public void setInvocationInfo(InvocationInfo info) {
        this.invInfo = info;
    }

    @Override
    public void setResult(Result result) {
        this.result = result;

    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public int getInputContentLength() {
        return -1;
    }

    @Override
    public void setOutputContentLength(int length) {

    }

    @Override
    public String getInputContentType() {
        return null;
    }

    @Override
    public void setOutputContentType(String contentType) {

    }

}