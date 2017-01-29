package com.smoner.rpc.demo2.framework.testclass;

import java.io.Serializable;

/**
 * Created by smoner on 2017/1/29.
 */
public interface RemoteProxy extends Serializable {

    public Object getAttribute(String name);

    public void setAttribute(String name, Object value);

    public ComponentMetaVO getComponentMetaVO();

    public int getRetryMax();

    public void setRetryMax(int retryMax);

    public long getRetryInterval();

    public void setRetryInterval(long l);

    public void setRemoteAddressSelector(RemoteAddressSelector ras);

    public RemoteAddressSelector getRemoteAddressSelector();
}
