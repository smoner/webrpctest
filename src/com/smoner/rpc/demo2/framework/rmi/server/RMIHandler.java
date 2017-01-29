package com.smoner.rpc.demo2.framework.rmi.server;

import java.io.IOException;

/**
 * Created by smoner on 2017/1/27.
 */
public interface RMIHandler {
    public void handle(RMIContext context) throws IOException;
}
