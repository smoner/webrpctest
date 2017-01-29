package com.smoner.rpc.demo2.framework.comn;


import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.smoner.rpc.demo2.framework.core.JndiContext;
import com.smoner.rpc.demo2.framework.core.ServiceComponent;
import com.smoner.rpc.demo2.framework.exception.ComponentNotFoundException;
import com.smoner.rpc.demo2.framework.exception.FrameworkRuntimeException;

/**
 * Created by UFSoft.
 * User: 何冠宇
 * Date: 2005-1-22
 * Time: 15:12:06
 */
public class JndiContextComponent implements ServiceComponent, JndiContext {

    private Map<String, Object> objectMap;

    private Map<String, Object> remoteSTLSMap;

    private InitialContext ctx;

    private boolean bStarted;

    private Properties ctxProperties;

    public JndiContextComponent() {
        this(null);
    }

    /**
     * create the JNDIHelper which have the properities
     *
     * @param props
     */
    public JndiContextComponent(Properties props) {
        this.ctxProperties = props;

        objectMap = new ConcurrentHashMap<String, Object>(128);

        remoteSTLSMap = new ConcurrentHashMap<String, Object>(128);
    }

    /**
     * Get the initial context
     *
     * @return
     */
    public Context getInitialContext() {
        if (ctx == null) {
            try {
                if (ctxProperties == null) {
                    ctx = new InitialContext();
                } else {
                    ctx = new InitialContext(ctxProperties);
                }
            } catch (Exception exp) {
                throw new FrameworkRuntimeException("Initial Contntext create error", exp);
            }
        }
        return ctx;
    }

    /**
     * The basic jndi lookupup method to lookup original object from jndi tree
     *
     * @param jndiName
     * @return
     * @throws ComponentNotFoundException
     */
    public Object lookup(String jndiName) throws ComponentNotFoundException {

        Object object = objectMap.get(jndiName);
        if (object == null) {
            object = lookupWithoutCache(jndiName);
            objectMap.put(jndiName, object);
        }
        return object;
    }

    public Object lookupWithoutCache(String jndiName) throws ComponentNotFoundException {
        Context ctx = getInitialContext();
        try {
            return ctx.lookup(jndiName);
        } catch (NamingException e) {
            throw new ComponentNotFoundException("Component not found from JNDI: " + jndiName, e);
        }
    }

    public void removeFromCache(String jndiName){
        objectMap.remove(jndiName);
    }

    public void start() throws Exception {
        bStarted = true;
    }

    public void stop() throws Exception {
        bStarted = false;
        objectMap.clear();
        remoteSTLSMap.clear();
        if (ctx != null) {
            ctx.close();
            ctx = null;
        }
    }

    public boolean isStarted() {
        return bStarted;
    }
}