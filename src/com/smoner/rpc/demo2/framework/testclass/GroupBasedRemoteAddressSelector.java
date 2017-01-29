package com.smoner.rpc.demo2.framework.testclass;

import com.smoner.rpc.demo2.framework.exception.ConnectorException;

import java.net.MalformedURLException;

/**
 * Created by smoner on 2017/1/29.
 */
public class GroupBasedRemoteAddressSelector  implements RemoteAddressSelector {

    private static final long serialVersionUID = -300090652128749483L;

    private Address dispatchAddress;

    private String serverGroup;

    public GroupBasedRemoteAddressSelector(Address addr, String serverGroup) {
        this.dispatchAddress = addr;
        this.serverGroup = serverGroup;
    }

    @Override
    public Address select() throws ConnectorException {
        Address t = dispatchAddress;

        try {
            if (serverGroup != null && !"framework".equals(serverGroup)) {
                if (t.getPath().endsWith("/")) {
                    t = new Address(t + serverGroup);
                } else {
                    t = new Address(t + "/" + serverGroup);
                }
            }
        } catch (MalformedURLException e) {

        }

        return t;
    }

    @Override
    public void fail(Address url) {
    }

    public int hashCode() {
        return (serverGroup == null ? 0 : serverGroup.hashCode())
                + dispatchAddress.hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof GroupBasedRemoteAddressSelector) {
            GroupBasedRemoteAddressSelector ga = (GroupBasedRemoteAddressSelector) other;
            if (ga.dispatchAddress.equals(dispatchAddress)) {
                return equals(ga.serverGroup, serverGroup);
            }
        }
        return false;
    }

    private boolean equals(String a, String b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }
}
