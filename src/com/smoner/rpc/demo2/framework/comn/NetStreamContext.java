package com.smoner.rpc.demo2.framework.comn;


import java.util.HashMap;
import java.util.Map;


import org.granite.lang.util.WeakHashSet;

public final class NetStreamContext {

    private static TL clientThreadToken = new TL();

    private static final class ByteRef {
        private ByteRef() {
            synchronized (ws) {
                ws.add(this);
            }
        }

        public byte[] bytes;
    }
    private static WeakHashSet<ByteRef> ws = new WeakHashSet<ByteRef>();
    private static final class TL extends InheritableThreadLocal<ByteRef> {

        protected ByteRef childValue(ByteRef parentValue) {
            return parentValue;
        }

        public void setBytes(byte[] bytes) {
            ByteRef br = get();
            if (br != null) {
                br.bytes = bytes;
            } else {
                if (bytes == null) {
                    return;
                }
                br = new ByteRef();
                br.bytes = bytes;
                set(br);
            }
        }

        public byte[] getBytes() {
            ByteRef br = get();
            if (br != null) {
                return br.bytes;
            } else {
                return null;
            }
        }

    }
    private static ThreadLocal<byte[]> serverThreadToken = new ThreadLocal<byte[]>();

    private static Map<String, byte[]> clientTokens = new HashMap<String, byte[]>(1);

    public static void setToken(byte[] t) {
        if (RuntimeEnv.getInstance().isRunningInServer()) {
            serverThreadToken.set(t);
        } else {
            clientThreadToken.setBytes(t);
        }
    }

    public static byte[] getToken() {
        if (RuntimeEnv.getInstance().isRunningInServer()) {
            return serverThreadToken.get();
        } else {
            return clientThreadToken.getBytes();
        }
    }

    public static byte[] getToken(String key) {
        synchronized (clientTokens) {
            return clientTokens.get(key);
        }
    }

    public static void setToken(String key, byte[] token) {
        if (RuntimeEnv.getInstance().isRunningInBrowser()) {
            synchronized (clientTokens) {
                if (token == null) {
                    clientTokens.remove(key);
                } else {
                    clientTokens.put(key, token);
                }
            }
        } else {
           // Logger.error("Running in server can't keep token.");
        }
    }
}
