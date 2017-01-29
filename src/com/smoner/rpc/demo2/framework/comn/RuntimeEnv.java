package com.smoner.rpc.demo2.framework.comn;

import java.util.Properties;

/**
 * Created by smoner on 2017/1/27.
 */
public class RuntimeEnv {
    private Properties abitraryProperty = new Properties();
    private ThreadLocal<Boolean> threadRunningInServer = new ThreadLocal<Boolean>() {
        protected Boolean initialValue() {
            return isRunningInServer();
        }
    };
    private Boolean runingServer;
    /**
     * the RuntimeEnv singleton implementation
     */
    private static RuntimeEnv runtimeEnv = new RuntimeEnv();

    /**
     * get the singleton RuntimeEnv instance
     *
     * @return
     */
    public static RuntimeEnv getInstance() {
        return runtimeEnv;
    }

    /**
     * check wheterh the code is running in server
     *
     * @return
     */
    public boolean isRunningInServer() {
        if (runingServer == null) {
            runingServer = "server".equals(System.getProperty("nc.run.side"));
        }

        return runingServer.booleanValue();
    }

    /**
     * check whether the code is running in browser.
     *
     * @return
     */
    public boolean isRunningInBrowser() {
        return !isRunningInServer();
    }

    public boolean isThreadRunningInServer() {
        return ((Boolean) threadRunningInServer.get()).booleanValue();
    }
    /**
     * store a named property in the runtime environment
     *
     * @param name
     * @param value
     */
    public void setProperty(String name, String value) {
        abitraryProperty.put(name, value);
    }

    /**
     * get a property with name
     *
     * @param name
     * @return
     */
    public String getProperty(String name) {
        return abitraryProperty.getProperty(name);
    }
}
