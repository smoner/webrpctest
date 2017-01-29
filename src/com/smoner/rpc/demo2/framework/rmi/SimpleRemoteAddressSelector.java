package com.smoner.rpc.demo2.framework.rmi;

import com.smoner.rpc.demo2.framework.testclass.Address;
import com.smoner.rpc.demo2.framework.testclass.RemoteAddressSelector;

/**
 * Created by smoner on 2017/1/29.
 */
public class SimpleRemoteAddressSelector implements RemoteAddressSelector {

    private static final long serialVersionUID = -2024092181526762009L;

    private Address address;

    public SimpleRemoteAddressSelector(Address address) {
        this.address = address;
    }

    @Override
    public Address select() {
        return address;
    }

    @Override
    public void fail(Address url) {

    }

    public int hashCode() {
        return address.hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof SimpleRemoteAddressSelector) {
            SimpleRemoteAddressSelector oa = (SimpleRemoteAddressSelector) other;
            return oa.address.equals(address);
        }

        return false;

    }

}
