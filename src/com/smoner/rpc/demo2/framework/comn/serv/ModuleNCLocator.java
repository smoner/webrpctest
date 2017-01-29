package com.smoner.rpc.demo2.framework.comn.serv;

import com.smoner.rpc.demo2.framework.comn.NCLocator;
import com.smoner.rpc.demo2.framework.exception.ComponentException;
import com.smoner.rpc.demo2.framework.naming.Context;

import java.util.Properties;

/**
 * Created by smoner on 2017/1/27.
 */
public class ModuleNCLocator extends NCLocator {

    private Context ctx = null;

    protected void init(Properties env) {
//        ctx = BusinessAppServer.getInstance().getContext(getProperty(env, TARGET_MODULE));
    }

    public Object lookup(String name) throws ComponentException {
        Object obj = ctx.lookup(name);
/*        for (int i = 0; i < 3 && (obj instanceof Resolvable); i++) {
            try {
               // Logger.warn("wait componet construct finished:" + name);
                Thread.sleep(3);
                obj = ctx.lookup(name);
            } catch (InterruptedException e) {
            }
        }
        if (obj instanceof Resolvable) {
            throw new ComponentException(name, " component now is constructing...");
        }*/
        return obj;

    }
}
