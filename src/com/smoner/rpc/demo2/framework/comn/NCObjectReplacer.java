package com.smoner.rpc.demo2.framework.comn;

import com.smoner.rpc.demo2.framework.pub.lang.UFBoolean;
import com.smoner.rpc.demo2.framework.pub.lang.UFDate;
import com.smoner.rpc.demo2.framework.pub.lang.UFDateTime;
import com.smoner.rpc.demo2.framework.pub.lang.UFDouble;

import java.util.HashMap;

/**
 * Created by smoner on 2017/1/29.
 */

public class NCObjectReplacer implements ObjectReplacer {

    private HashMap<Long, byte[]> dttimeSet = new HashMap<Long, byte[]>();

    private HashMap<UD, byte[]> dbSet = new HashMap<UD, byte[]>();

    private HashMap<Long, byte[]> dtSet = new HashMap<Long, byte[]>();

    public Object replaceObject(Object object) {
        if (object instanceof UFDate) {
            UFDate d = (UFDate) object;
            return internDate(dtSet, UF_DATE, d.getMillis());
        } else if (object instanceof UFDateTime) {
            UFDateTime dt = (UFDateTime) object;
            return internDate(dttimeSet, UF_DATETIME, dt.getMillis());
        } else if (object instanceof UFBoolean) {
            UFBoolean b = (UFBoolean) object;
            if (b.booleanValue()) {
                return TRUE;
            } else {
                return FALSE;
            }
        } else if (object instanceof UFDouble) {
            UFDouble ud = (UFDouble) object;
            return interDouble(ud);
        } else if (object instanceof String) {
            return ((String) object).intern();
        }
        return object;
    }

    private Object internDate(HashMap<Long, byte[]> map, byte t, long l) {
        byte[] ret = map.get(l);
        if (ret == null) {
            ret = new byte[10];
            ret[0] = t;
            ret[9] = UF_END;
            toByte(ret, 1, l);
            map.put(l, ret);
        }

        return ret;

    }

    private void toByte(byte[] bytes, int off, long v) {
        bytes[0 + off] = (byte) (v >>> 56);
        bytes[1 + off] = (byte) (v >>> 48);
        bytes[2 + off] = (byte) (v >>> 40);
        bytes[3 + off] = (byte) (v >>> 32);
        bytes[4 + off] = (byte) (v >>> 24);
        bytes[5 + off] = (byte) (v >>> 16);
        bytes[6 + off] = (byte) (v >>> 8);
        bytes[7 + off] = (byte) (v >>> 0);

    }

    private void toBytes(byte[] bytes, int from, long v) {
        bytes[from] = (byte) (v >>> 56);
        bytes[from + 1] = (byte) (v >>> 48);
        bytes[from + 2] = (byte) (v >>> 40);
        bytes[from + 3] = (byte) (v >>> 32);
        bytes[from + 4] = (byte) (v >>> 24);
        bytes[from + 5] = (byte) (v >>> 16);
        bytes[from + 6] = (byte) (v >>> 8);
        bytes[from + 7] = (byte) (v >>> 0);
    }

    private byte[] interDouble(UFDouble dbl) {
        UD ud = new UD(dbl);
        byte[] ret = dbSet.get(ud);
        if (ret == null) {
            ret = new byte[45];
            ret[0] = UF_DOUBLE;
            ret[1] = (byte) dbl.getPower();
            ret[2] = dbl.getSIValue();
            long[] av = dbl.getDV();
            int zeroStart = -1;
            for (int i = 0; i < av.length; i++) {
                if (av[i] == 0) {
                    if (zeroStart == -1)
                        zeroStart = i;
                    continue;
                } else {
                    zeroStart = -1;
                }
                toBytes(ret, 4 + i * 8, (long) av[i]);
            }

            int len = 45;
            if (zeroStart != -1) {
                len = zeroStart * 8 + 4;
            }

            for (int i = len - 1; i > 3; i--) {
                if (ret[i] == 0) {
                    len--;
                } else {
                    break;
                }
            }

            ret[3] = (byte) (len + 1);
            ret[len] = UF_END;

            if (len < 44) {
                byte[] b = new byte[ret[3]];
                System.arraycopy(ret, 0, b, 0, ret[3]);
                ret = b;
            }
            dbSet.put(ud, ret);
        }
        return ret;
    }

    private static class UD {
        private UFDouble u;

        int hashCode;

        private UD(UFDouble u) {

            this.u = u;
            if (u != null) {
                hashCode = u.hashCode() + u.getPower() * 29;
            }
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object o) {
            if (o instanceof UD) {
                UD o1 = (UD) o;
                if (o1.u == null && u == null) {
                    return true;
                } else if (o1.u != null && u != null) {
                    return o1.u.equals(u) && o1.u.getPower() == u.getPower();
                }

            }
            return false;
        }

    }

}