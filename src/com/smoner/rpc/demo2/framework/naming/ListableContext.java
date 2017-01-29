package com.smoner.rpc.demo2.framework.naming;
public interface ListableContext extends Context {
    public String[] listNames();

    public String[] listNames(Class<?> intfClazz);

    //public ComponentMetaVO getComponentMetaVO(String name) throws ComponentException;
}