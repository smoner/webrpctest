package com.smoner.rpc.demo2.framework.core;

/**
 * Created by smoner on 2017/1/27.
 */
public interface ServiceComponent extends ActiveComponent {
    /**
     * startModule the service and allocate resources
     *
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * stopModule the service and release resources
     *
     * @throws Exception
     */
    public void stop() throws Exception;

    /**
     * check whether the service is started
     */
    public boolean isStarted();
}
