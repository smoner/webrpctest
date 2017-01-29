package com.smoner.rpc.demo2.framework.comn;

import com.smoner.rpc.demo2.framework.pub.lang.UFBoolean;
import com.smoner.rpc.demo2.framework.pub.lang.UFDate;
import com.smoner.rpc.demo2.framework.pub.lang.UFDateTime;
import com.smoner.rpc.demo2.framework.pub.lang.UFDouble;

/**
 * Created by smoner on 2017/1/27.
 */
public class NCObjectResolver implements ObjectResolver {

    public Object resolveObject(Object obj) {

        if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;

            if (bytes.length >= UF_BOOLEAN_LEN
                    && bytes[bytes.length - 1] == UF_END) {
                if (bytes.length == UF_BOOLEAN_LEN && bytes[0] == UF_BOOLEAN) {
                    if (bytes[1] == 1) {
                        return UFBoolean.TRUE;
                    } else if (bytes[1] == 0) {
                        return UFBoolean.FALSE;
                    }
                } else if (bytes.length == UF_DATE_LEN && bytes[0] == UF_DATE) {
                    return UFDate.getDate(toLong(bytes, 1));
                } else if (bytes.length == UF_DATETIME_LEN
                        && bytes[0] == UF_DATETIME) {
                    return new UFDateTime(toLong(bytes, 1));
                } else if (bytes.length > 4 && bytes[0] == UF_DOUBLE
                        && bytes[bytes.length - 1] == UF_END
                        && bytes[3] == bytes.length) {
                    return toUFDouble(bytes);
                }
            }
        }

        return obj;

    }

    private static long toLong(byte[] b, int off) {
        return ((b[7 + off] & 0xFFL) << 0) + ((b[6 + off] & 0xFFL) << 8)
                + ((b[5 + off] & 0xFFL) << 16) + ((b[4 + off] & 0xFFL) << 24)
                + ((b[3 + off] & 0xFFL) << 32) + ((b[2 + off] & 0xFFL) << 40)
                + ((b[1 + off] & 0xFFL) << 48) + ((b[0 + off] & 0xFFL) << 56);
    }

    private UFDouble toUFDouble(byte[] b) {

        int power;

        byte si;

        long[] dv;
        power = b[1];
        si = b[2];
        dv = new long[5];

        int dvlen = (b[3] - 5) / 8 + (((b[3] - 5) % 8) == 0 ? 0 : 1);

        byte[] nb = new byte[dvlen * 8];

        System.arraycopy(b, 4, nb, 0, b.length - 5);

        b = nb;

        for (int i = 0; i < dvlen; i++) {
            dv[i] = toLong(b, i * 8);
        }

        int v = 0;
        for (int i = 0; i < dv.length; i++) {
            v += dv[i];
        }

        if (v == 0) {
            return UFDouble.ZERO_DBL;
        } else if (v == 1 && dv[1] == 1 && si == 1 && power == -8) {
            return UFDouble.ONE_DBL;
        } else {
            return new UFDouble(dv, si, power);
        }

    }
}
