package com.smoner.rpc.demo2.framework.comn;


import com.smoner.rpc.demo2.framework.exception.FrameworkRuntimeException;
import com.smoner.rpc.demo2.framework.naming.Context;
import com.smoner.rpc.demo2.framework.util.ClassUtil;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public abstract class NCLocator implements Context {

    private static final RuntimeEnv runtimeEnv = RuntimeEnv.getInstance();

    public static final String SERVICEDISPATCH_URL = "SERVICEDISPATCH_URL";

    public static final String CLIENT_COMMUNICATOR = "CLIENT_COMMUNICATOR";

    public static final String TARGET_MODULE = "nc.targetModule";

    private static NCLocator DEFAULT_SERVER_LOCATOR = null;

    private static NCLocator DEFAULT_CLIENT_LOCATOR = null;

    private static Map<String, NCLocator> locatorMap = new ConcurrentHashMap<String, NCLocator>();

    /**
     * the NCLocator implementation class property
     */
    public static final String LOCATOR_PROVIDER_PROPERTY = "nc.locator.provider";

    /**
     * 客户端的NCLocator实现类名称
     */
    private static final String CLIENT_LOCATOR = "nc.bs.framework.rmi.RmiNCLocator";// "nc.bs.framework.comn.cli.ClientNCLocator";

    /**
     * 服务端的NCLocator实现名称
     */
    private static final String SERVER_LOCATOR = "nc.bs.framework.server.ServerNCLocator";

    /**
     * 根据环境获取NCLocator的实现，环境的设置通过RuntimeEnv进行
     *
     * @see RuntimeEnv
     * @return
     */
    public static NCLocator getInstance() {
        return getInstance(null);
    }

    /**
     * 根据传递的环境属性获取特定的NCLocator实现。主要的参数为nc.locator.provider,设置 NCLocator地实现
     *
     * @param props
     * @return
     */
    public static NCLocator getInstance(Properties props) {
        NCLocator locator = null;

        String svcDispatchURL = getProperty(props, SERVICEDISPATCH_URL);
        String locatorProvider = getProperty(props, LOCATOR_PROVIDER_PROPERTY);
        String targetModule = getProperty(props, TARGET_MODULE);

        String key = ":" + svcDispatchURL + ":" + locatorProvider + ":"
                + targetModule;
        locator = locatorMap.get(key);
        if (locator != null) {
            return locator;
        }
        if (!isEmpty(locatorProvider)) {

            locator = newInstance(locatorProvider);
        } else {
            if (!isEmpty(svcDispatchURL)) {
                locator = newInstance(CLIENT_LOCATOR);
            } else {
                locator = getDefaultLocator();
            }
        }

        locator.init(props);
        locatorMap.put(key, locator);
        return locator;
    }

    /**
     * 获取默认的NCLocator实现，通过RuntimeEnv获得运行环境的属性
     *
     * @return
     */
    private static NCLocator getDefaultLocator() {

        if (RuntimeEnv.getInstance().isThreadRunningInServer()) {
            if (DEFAULT_SERVER_LOCATOR == null) {
                synchronized (NCLocator.class) {
                    if (DEFAULT_SERVER_LOCATOR == null) {
                        DEFAULT_SERVER_LOCATOR = newInstance(SERVER_LOCATOR);
                    }
                }
            }
            return DEFAULT_SERVER_LOCATOR;
        } else {
            if (DEFAULT_CLIENT_LOCATOR == null) {
                synchronized (NCLocator.class) {
                    if (DEFAULT_CLIENT_LOCATOR == null) {
                        DEFAULT_CLIENT_LOCATOR = newInstance(CLIENT_LOCATOR);
                    }
                }
            }
            return DEFAULT_CLIENT_LOCATOR;
        }

    }

    /**
     * 创建一个NCLocator实例
     *
     * @param name
     * @return
     */
    private static NCLocator newInstance(String name) {
        try {
            return (NCLocator) ClassUtil.loadClass(name).newInstance();
        } catch (Exception e) {
            throw new FrameworkRuntimeException("Can't find the class: " + name);
        }

    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 初始化NCLocator地实现，不同的实现的初始环的环境属性不同
     *
     * @param env
     */
    abstract protected void init(Properties env);

    protected static String getProperty(Properties env, String name) {
        String value = env == null ? null : env.getProperty(name);
        if (value == null) {
            value = InvocationInfoProxy.getInstance().getProperty(name);
            if (value == null)
                value = runtimeEnv.getProperty(name);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> clazz) {
        return (T) lookup(clazz.getName());
    }

}