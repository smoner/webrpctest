package com.smoner.rpc.demo2.framework.pub.lang;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
/**
 * Created by smoner on 2017/1/27.
 */

@SuppressWarnings("rawtypes")
public class UFDouble extends java.lang.Number implements java.io.Serializable,
        Comparable {
    static final long serialVersionUID = -809396813980155342L;

    private int power = DEFAULT_POWER;

    /**
     * Rounding mode to round away from zero. Always increments the digit prior
     * to a non-zero discarded fraction. Note that this rounding mode never
     * decreases the magnitude of the calculated value.
     */
    public final static int ROUND_UP = 0;

    /**
     * Rounding mode to round towards zero. Never increments the digit prior to
     * a discarded fraction (i.e., truncates). Note that this rounding mode
     * never increases the magnitude of the calculated value.
     */
    public final static int ROUND_DOWN = 1;

    /**
     * Rounding mode to round towards positive infinity. If the BigDecimal is
     * positive, behaves as for <tt>ROUND_UP</tt>; if negative, behaves as for
     * <tt>ROUND_DOWN</tt>. Note that this rounding mode never decreases the
     * calculated value.
     */
    public final static int ROUND_CEILING = 2;

    /**
     * Rounding mode to round towards negative infinity. If the BigDecimal is
     * positive, behave as for <tt>ROUND_DOWN</tt>; if negative, behave as for
     * <tt>ROUND_UP</tt>. Note that this rounding mode never increases the
     * calculated value.
     */
    public final static int ROUND_FLOOR = 3;

    /**
     * Rounding mode to round towards "nearest neighbor" unless both neighbors
     * are equidistant, in which case round up. Behaves as for <tt>ROUND_UP</tt>
     * if the discarded fraction is &gt;= .5; otherwise, behaves as for
     * <tt>ROUND_DOWN</tt>. Note that this is the rounding mode that most of us
     * were taught in grade school.
     */
    public final static int ROUND_HALF_UP = 4;

    /**
     * Rounding mode to round towards "nearest neighbor" unless both neighbors
     * are equidistant, in which case round down. Behaves as for
     * <tt>ROUND_UP</tt> if the discarded fraction is &gt; ...5; otherwise,
     * behaves as for <tt>ROUND_DOWN</tt>.
     */
    public final static int ROUND_HALF_DOWN = 5;

    /**
     * Rounding mode to round towards the "nearest neighbor" unless both
     * neighbors are equidistant, in which case, round towards the even
     * neighbor. Behaves as for ROUND_HALF_UP if the digit to the left of the
     * discarded fraction is odd; behaves as for ROUND_HALF_DOWN if it's even.
     * Note that this is the rounding mode that minimizes cumulative error when
     * applied repeatedly over a sequence of calculations.
     */
    public final static int ROUND_HALF_EVEN = 6;

    /**
     * Rounding mode to assert that the requested operation has an exact result,
     * hence no rounding is necessary. If this rounding mode is specified on an
     * operation that yields an inexact result, an <tt>ArithmeticException</tt>
     * is thrown.
     */
    public final static int ROUND_UNNECESSARY = 7;

    /** MAX ARRAY LENGTH 表示最大有效位数 6 × 9 */
    private final static int ARRAY_LENGTH = 5;

    private final static int EFFICIENCY_SEATE = 16;

    /** 掩码 (long) Math.pow(10, EFFICIENCY_SEATE); 注意要和上面的同步更改 */
    private final static long MAX_ONELONG_VALUE = (long) 1E16;

    /** power V1 E POWER 1234566E-4 power 取值 0~-9 */
    private final static long POWER_ARRAY[];

    /**
     * 1、2舍位；3~7取5；8、9进位。注意：对这种舍位机制，如果UFDouble的 power = -n， 则在 -n
     * 位上进行计算；对上述其他机制，如果UFDouble的 power = -n，则在 -（n+1) 位上进行舍位计算。
     */
    public final static int ROUND_TO_ZERO_AND_HALF = 8;

    private byte si = 1;

    /** 整数位V */
    private long v[] = new long[ARRAY_LENGTH];
    static {
        POWER_ARRAY = new long[EFFICIENCY_SEATE + 2];
        for (int i = 0; i < POWER_ARRAY.length - 1; i++) {
            POWER_ARRAY[i] = (long) Math.pow(10, EFFICIENCY_SEATE - i);
        }
        POWER_ARRAY[POWER_ARRAY.length - 1] = 0;
    }

    public static UFDouble ONE_DBL = new UFDouble(1f);

    public static UFDouble ZERO_DBL = new UFDouble(0f);

    /**
     * UFDouble 构造子注解。
     */
    public UFDouble() {
        super();
    }

    /**
     * 初始化一个对象 double value<br>
     * default power is -8;
     *
     *
     */
    public UFDouble(double d) throws NumberFormatException {
        this(d, DEFAULT_POWER);
    }

    /**
     * 初始化一个对象 double value<br>
     * newPower 指数<br>
     */
    public UFDouble(double d, int newPower) throws NumberFormatException {
        setValue(d, newPower);
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-5-30 9:55:31)
     *
     * @param d
     *            int
     */
    public UFDouble(int d) {
        this((long) d);
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-5-30 9:58:20)
     *
     * @param d
     *            int
     * @param pow
     *            int
     */
    public UFDouble(int d, int pow) {
        this((long) d, pow);
    }

    public UFDouble(long d) {
        this(d, DEFAULT_POWER);
    }

    public UFDouble(long d, int pow) throws NumberFormatException {
        this(d + 0.0, pow);
    }

    /**
     * 进行对象的反序列化，增加构造
     *
     * @param dv
     * @param si
     * @param pow
     * @throws NumberFormatException
     */
    public UFDouble(long[] dv, byte si, int pow) throws NumberFormatException {
        if (dv == null || dv.length != ARRAY_LENGTH) {
            throw new NumberFormatException("array length must be 5");
        }
        this.v = dv;
        this.si = si;
        this.power = pow;
    }

    /**
     * 以DEFAULT_POWER精度构造
     *
     * @param d
     * @throws NumberFormatException
     */
    public UFDouble(Double d) throws NumberFormatException {
        this(d.doubleValue(), DEFAULT_POWER);
    }

    /**
     * 以DEFAULT_POWER精度构造
     *
     * @param d
     * @throws NumberFormatException
     */
    public UFDouble(String str) throws NumberFormatException {
        initByString(str);
    }

    private void initByString(String str) {
        String s = "";
        int npower = DEFAULT_POWER;
        if (str == null || str.trim().length() == 0) {
            s = "0";
        } else {
            java.util.StringTokenizer token = new java.util.StringTokenizer(
                    str, ",");
            while (token.hasMoreElements()) {
                s += token.nextElement().toString();
            }

            int pos = s.indexOf('e');
            pos = pos < 0 ? s.indexOf('E') : pos;
            if (pos >= 0) {
                try {
                    npower = Integer.parseInt(s.substring(pos + 1));
                } catch (Throwable t) {
                    npower = DEFAULT_POWER;
                }
                npower = getEPower(s, npower, pos);
                setValue(Double.parseDouble(s), npower);
                return;
            }
            if (s.charAt(0) == '-') {
                si = -1;
                s = s.substring(1);
            } else if (s.charAt(0) == '+')
                s = s.substring(1);
        }

        int loc = s.indexOf('.');

        if (loc >= 0) {
            npower = s.length() - (loc + 1);
        } else {
            npower = 0;
        }

        fromString(npower, s);
    }

    private int getEPower(String s, int ePower, int eindex) {
        int decimalIndex = s.indexOf(".");
        boolean hasDecimalDot = decimalIndex > 0 ? true : false;
        int revisePower = 0;// 校正指数power;
        if (hasDecimalDot) {
            int decimaDigits = eindex - decimalIndex - 1;
            if (ePower > 0) {
                revisePower = ePower - decimaDigits >= 0 ? 0 : ePower
                        - decimaDigits;
            } else {
                revisePower = ePower - decimaDigits;
            }
        } else {
            revisePower = ePower >= 0 ? 0 : ePower;
        }
        return revisePower;
    }

    /**
     * 以newPower精度构造
     *
     * @param d
     * @throws NumberFormatException
     */
    public UFDouble(String str, int newPower) throws NumberFormatException{
        String s = "";
        if (str == null || str.trim().length() == 0) {
            s = "0";
        } else {
            java.util.StringTokenizer token = new java.util.StringTokenizer(
                    str, ",");
            while (token.hasMoreElements()) {
                s += token.nextElement().toString();
            }
            if (s.indexOf('e') >= 0 || s.indexOf('E') >= 0) {
                setValue(Double.parseDouble(s), getValidPower(newPower));
                return;
            }
            if (s.charAt(0) == '-') {
                si = -1;
                s = s.substring(1);
            } else if (s.charAt(0) == '+')
                s = s.substring(1);
        }
        fromString(newPower, s);
    }

    private void fromString(int newPower, String s) {
        newPower = getValidPower(newPower);
        int loc = s.indexOf('.');
        if (loc >= 0) {
            String s1 = s.substring(loc + 1);
            if (s1.length() > -newPower) {
                if (-newPower >= EFFICIENCY_SEATE)
                    s1 = s1.substring(0, EFFICIENCY_SEATE);
                else
                    s1 = s1.substring(0, 1 - newPower);
            }

            power = newPower;
            for (int i = s1.length(); i < EFFICIENCY_SEATE; i++)
                s1 += "0";
            v[0] = Long.parseLong(s1);
            s = s.substring(0, loc);
        } else {
            power = newPower;
            v[0] = 0;
        }

        int len = s.length();
        int sitLoc = 1;
        while (len > 0) {
            String s1 = "";
            if (len > EFFICIENCY_SEATE) {
                s1 = s.substring(len - EFFICIENCY_SEATE);
                s = s.substring(0, len - EFFICIENCY_SEATE);
            } else {
                s1 = s;
                s = "";
            }
            len = s.length();
            v[sitLoc++] = Long.parseLong(s1);
        }
        for (int i = sitLoc; i < v.length; i++)
            v[i] = 0;
        round(ROUND_HALF_UP);
    }

    /**
     * 根据一个BigDecimal进行构造，精度与BigDecimal一致
     *
     * @param value
     */
    public UFDouble(java.math.BigDecimal value) {
        if (value.toString().length() <= EFFICIENCY_SEATE) {
            setValue(value.doubleValue(), value.scale());
        } else {
            initByString(value.toString());
        }
    }

    public UFDouble(UFDouble fd) {
        si = fd.si;
        for (int i = 0; i < v.length; i++) {
            v[i] = fd.v[i];
        }
        power = fd.power;
    }

    /**
     * 加上一个数
     */
    public UFDouble add(double d1) {
        return add(new UFDouble(d1));
    }

    /**
     * 加上一个数
     */
    public UFDouble add(UFDouble ufd) {
        int power = Math.abs(ufd.getPower()) > Math.abs(getPower()) ? ufd
                .getPower() : getPower();

        return add(ufd, power, ROUND_HALF_UP);
    }

    /**
     * 加上一个数
     */
    public UFDouble add(UFDouble ufd, int newPower) {
        return add(ufd, newPower, ROUND_HALF_UP);
    }

    /**
     * 加上一个数 ufd<br>
     *
     * @param newPower
     *            指数<br>
     * @param roundingMode
     *            进位方式<br>
     */
    public UFDouble add(UFDouble ufd, int newPower, int roundingMode) {
        newPower = getValidPower(newPower);

        UFDouble fdnew = new UFDouble(this);

        fdnew.power = newPower;
        fdnew.addUp0(ufd, newPower, roundingMode);
        return fdnew;
    }

    /**
     * 加上一个数
     */
    private void addUp0(double ufd) {
        addUp0(new UFDouble(ufd), power, ROUND_HALF_UP);
    }

    /**
     * 加上一个数 ufd<br>
     *
     * @param newPower
     *            指数<br>
     * @param roundingMode
     *            进位方式<br>
     */
    private void addUp0(UFDouble ufd, int newPower, int roundingMode) {
        toPlus();
        ufd.toPlus();
        for (int i = 0; i < v.length; i++) {
            v[i] += ufd.v[i];
        }
        judgNegative();
        adjustIncluedFs();
        /** 将toPlus对 ufd 进行的符号变位进行调整回来 */
        ufd.judgNegative();
        round(roundingMode);
    }

    /**
     * 在各个位数上进行了处理后需要将数值进行调整过来， 出现了BUG，没有将负数情况没有将负数的情况考虑进去。
     *
     * @exception 异常描述
     * @see 需要参见的其它内容
     * @since 从类的那一个版本，此方法被添加进来。（可选）
     * @deprecated该方法从类的那一个版本后，已经被其它方法替
     */
    private void adjustIncluedFs() {
        for (int i = 1; i < v.length; i++) {
            if (v[i - 1] < 0) {
                v[i]--;
                v[i - 1] += MAX_ONELONG_VALUE;
            } else {
                v[i] = v[i] + v[i - 1] / MAX_ONELONG_VALUE;
                v[i - 1] = v[i - 1] % MAX_ONELONG_VALUE;
            }
        }
    }

    private void adjustNotIncluedFs() {
        for (int i = 1; i < v.length; i++) {
            v[i] = v[i] + v[i - 1] / MAX_ONELONG_VALUE;
            v[i - 1] = v[i - 1] % MAX_ONELONG_VALUE;
        }
    }

    public int compareTo(Object o) {
        return toBigDecimal().compareTo(((UFDouble) o).toBigDecimal());
    }

    private void cutdown() {
        int p = -power;
        v[0] = v[0] / POWER_ARRAY[p] * POWER_ARRAY[p];
    }

    public UFDouble div(double d1) {
        UFDouble ufd = new UFDouble(d1);
        return div(ufd);
    }

    public UFDouble div(UFDouble ufd) {
        return div(ufd, DEFAULT_POWER);
    }

    public UFDouble div(UFDouble ufd, int power) {
        return div(ufd, power, ROUND_HALF_UP);
    }

    /**
     * 除上一个数
     */
    public UFDouble div(UFDouble ufd, int power, int roundingMode) {
        int newPower = getValidPower(power);
        BigDecimal bd = toBigDecimal();
        BigDecimal divisor = ufd.toBigDecimal();
        int maxScale = divisor.scale() > bd.scale() ? divisor.scale() : bd
                .scale();
        int nPower = Math.abs(power);
        maxScale = maxScale > nPower ? maxScale : nPower;
        //chenbina 20140710
        maxScale++;//保证多一位
        BigDecimal newbd = bd.divide(divisor, maxScale, RoundingMode.DOWN);//该处除法不允许进位
        UFDouble ufdNew = new UFDouble(newbd);
        return ufdNew.setScale( newPower, roundingMode );//返回结果处进位，这样保证了计算完毕只有一次进位
    }

    /**
     * 取得他的值<br>
     */
    public double doubleValue() {

		/*
		 * double d = 0; for (int i = v.length - 1; i >= 0; i--) { d *=
		 * MAX_ONELONG_VALUE; d += v[i]; } d /= MAX_ONELONG_VALUE; return d *
		 * si;
		 */

        return this.toDouble();
    }

    public float floatValue() {
        return (float) getDouble();
    }

    /**
     * 取得他的值<br>
     */
    public double getDouble() {
        return this.doubleValue();
    }

    /**
     * 该方法不推荐外部使用
     *
     * @return
     */
    public long[] getDV() {
        return this.v;
    }

    /**
     * 该方法不推荐外部使用
     *
     * @return
     */
    public byte getSIValue() {
        return this.si;
    }

    /**
     * Returns the value of the specified number as an <code>int</code>. This
     * may involve rounding.
     *
     * @return the numeric value represented by this object after conversion to
     *         type <code>int</code>.
     */
    public int intValue() {
        return (int) getDouble();
    }

    /**
     * 判断当前的数值是否是负数，并作必要的调整
     *
     * @exception 异常描述
     * @see 需要参见的其它内容
     * @since 从类的那一个版本，此方法被添加进来。（可选）
     * @deprecated该方法从类的那一个版本后，已经被其它方法替
     */
    private void judgNegative() {
        /** at first adjust if is 负数 */
        boolean isFs = false;
        for (int i = v.length - 1; i >= 0; i--) {
            if (v[i] < 0) {
                /** 是负数 */
                isFs = true;
                break;
            }
            if (v[i] > 0)
                break;
        }
        if (isFs) {
            for (int i = 0; i < v.length; i++)
                v[i] = -v[i];
            si = -1;
        }
    }

    public long longValue() {
        long d = 0;
        /** 去掉低位 */
        for (int i = v.length - 1; i > 0; i--) {
            d *= MAX_ONELONG_VALUE;
            d += v[i];
        }
        return d * si;
    }

    public UFDouble multiply(double d1) {
        /** 首先判断POWER的大小 */
        UFDouble ufD1 = new UFDouble(d1);
        return multiply(ufD1, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public UFDouble multiply(UFDouble ufd) {
        return multiply(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public UFDouble multiply(UFDouble ufd, int newPower) {
        return multiply(ufd, newPower, ROUND_HALF_UP);
    }

    /**
     * 乘上一个数 ufd<br>
     *
     * @param newPower
     *            指数<br>
     * @param roundingMode
     *            进位方式<br>
     */
    public UFDouble multiply(UFDouble ufd, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        BigDecimal bd = toBigDecimal();
        BigDecimal divisor = ufd.toBigDecimal();
        // int maxPrecious = divisor.precision() > bd.precision() ? divisor
        // .precision() : bd.precision();

        BigDecimal bdn = bd.multiply(divisor);
        bdn = bdn.setScale(-newPower, roundingMode);

        UFDouble ufdNew = new UFDouble(bdn);

        // ufdNew = ufdNew.setScale(newPower, roundingMode);
        return ufdNew;

    }

    /**
     * 按照当前的POWER，去掉小数不需要的部分 比如 999999.99123456 power = -2 result is
     * 999999.990000000 需要对各个进位方式进行考查 创建日期：(2001-4-11 15:35:15)
     *
     * @return ierp.pub.vo.data.UFDouble
     * @param d
     *            double
     * @param roundingtype
     *            int
     */
    private void round(int roundingMode) {
        boolean increment = true;
        switch (roundingMode) {
            case ROUND_UP:
                increment = true;
                break;
            case ROUND_CEILING:
                increment = si == 1;
                break;
            case ROUND_FLOOR:
                increment = si == -1;
                break;
            case ROUND_DOWN:
                increment = false;
                // si == -1;
                break;
            case ROUND_TO_ZERO_AND_HALF:
                // 一种特殊的舍位机制：1、2舍位；3~7取5；8、9进位。只处理正数：
			/*
			 * long l = (long)(d / Math.pow(10, newPower)); double fraction = d
			 * - l; if (fraction < 0.3) { fraction = 0; } else if (fraction <
			 * 0.8) { fraction = 0.5; } else { fraction = 1; } return new
			 * UFDouble(l + fraction, newPower);
			 */
        }
        int p = -power;
        long vxs = POWER_ARRAY[p + 1];
        /** 作内部运算 */
        if (increment) {
            v[0] += vxs * 5;
            adjustNotIncluedFs();
        }
        cutdown();
        // 为0时去掉负号
        boolean isZero = true;
        for (int i = 0; i < v.length; i++) {
            if (v[i] != 0) {
                isZero = false;
                break;
            }
        }
        if (si == -1 && isZero)
            si = 1;
        //
    }

    /**
     * @see #ROUND_UP
     * @see #ROUND_DOWN
     * @see #ROUND_CEILING
     * @see #ROUND_FLOOR
     * @see #ROUND_HALF_UP
     * @see #ROUND_HALF_DOWN
     * @see #ROUND_HALF_EVEN
     * @see #ROUND_UNNECESSARY
     */
    public UFDouble setScale(int power, int roundingMode) {
        UFDouble scaleDouble = null;
        if (this.power == power) {
            scaleDouble = (UFDouble) this.clone();
            scaleDouble.round(roundingMode);
        } else {
            int newPower = getValidPower(power);
            BigDecimal bd = toBigDecimal();
            bd = bd.setScale(-newPower, roundingMode);
            scaleDouble = new UFDouble(bd);
        }
        return scaleDouble;
    }

    /**
     * 初始化一个对象 double value<br>
     * newPower 指数<br>
     */
    private void setValue(double d, int newPower) throws NumberFormatException {
        double dd, ld;

        if (d < 0) {
            d = -d;
            si = -1;
        }
        dd = d;
        power = getValidPower(newPower);

        /** 不管POWER 如何内部统一使用小数点后9为处理 */
        double dxs = d % 1;
        d -= dxs;
        ld = d;
        for (int i = 1; i < v.length; i++) {
            v[i] = (long) (d % MAX_ONELONG_VALUE);
            d = d / MAX_ONELONG_VALUE;
        }
        long v2 = 0;
        if (dxs == 0.0)
            v2 = (long) (dxs * MAX_ONELONG_VALUE);
        else {
            if (dd / ld == 1.0) {
                dxs = 0.0;
                v2 = (long) (dxs * MAX_ONELONG_VALUE);
            } else {
                if (power <= -8) {
                    int iv = (int) v[2];
                    if (iv != 0) {
                        if (iv >= 1000000)
                            power = -0;
                        else if (iv >= 100000)
                            power = -1;
                        else if (iv >= 10000)
                            power = -2;
                        else if (iv >= 1000)
                            power = -3;
                        else if (iv >= 100)
                            power = -4;
                        else if (iv >= 10)
                            power = -5;
                        else if (iv >= 1)
                            power = -6;
                    } else {
                        iv = (int) v[1];
                        if (iv >= 100000000)
                            power = -7;
                    }
                    if (power < 0) {
                        int ii = -power;
                        double d1;
                        int i2 = 1;
                        double dxs1;
                        for (int i = 1; i < ii; i++) {
                            i2 *= 10;
                            dxs1 = ((double) Math.round(dxs * i2)) / i2;
                            d1 = ld + dxs1;
                            if (dd / d1 == 1.0) {
                                dxs = dxs1;
                                break;
                            }
                        }
                    }
                }
                v2 = (long) ((dxs + 0.00000000001) * MAX_ONELONG_VALUE);
            }
        }
        v[0] = v2;
        round(ROUND_HALF_UP);
    }

    public UFDouble sub(double d1) {
        UFDouble ufd = new UFDouble(d1);
        return sub(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public UFDouble sub(UFDouble ufd) {
        int power = Math.abs(ufd.getPower()) > Math.abs(getPower()) ? ufd
                .getPower() : getPower();
        return sub(ufd, power, ROUND_HALF_UP);
    }

    public UFDouble sub(UFDouble ufd, int newPower) {
        return sub(ufd, newPower, ROUND_HALF_UP);
    }

    /**
     * 减去一个数 ufd<br>
     *
     * @param newPower
     *            指数<br>
     * @param roundingMode
     *            进位方式<br>
     */
    public UFDouble sub(UFDouble ufd, int newPower, int roundingMode) {
        // newPower = newPower > 0 ? -newPower : ((newPower > -9) ? newPower :
        // -9);
        newPower = getValidPower(newPower);

        UFDouble ufdnew = new UFDouble(ufd);
        ufdnew.si = (byte) -ufdnew.si;
        return add(ufdnew, newPower, roundingMode);
    }

    /**
     *
     * 计算一组数据<br>
     * 每一步进行ROUND计算<br>
     *
     */
    public static UFDouble sum(double[] dArray) {
        return sum(dArray, DEFAULT_POWER);
    }

    /**
     *
     * 计算一组数据<br>
     * 每一步进行ROUND计算<br>
     *
     */
    public static UFDouble sum(double[] dArray, int newPower) {
        // newPower = newPower > 0 ? -newPower : ((newPower > -9) ? newPower :
        // -9);
        newPower = getValidPower(newPower);

        UFDouble ufd = new UFDouble(0, newPower);
        for (int i = 0; i < dArray.length; i++) {
            ufd.addUp0(dArray[i]);
        }
        return ufd;
    }

    /**
     *
     * 计算一组数据<br>
     * 每一步进行ROUND计算<br>
     *
     */

    public static UFDouble sum(double[] dArray, int newPower, int roundingMode) {
        // newPower = newPower > 0 ? -newPower : ((newPower > -9) ? newPower :
        // -9);
        newPower = getValidPower(newPower);

        UFDouble ufd = new UFDouble(0, newPower);
        for (int i = 0; i < dArray.length; i++) {
            UFDouble ufdNew = new UFDouble(dArray[i], newPower);
            ufd.addUp0(ufdNew, newPower, roundingMode);
        }
        return ufd;
    }

    /**
     * 转换为BigDecimal。
     * <p>
     * 创建日期：(2001-4-17 14:45:43)
     *
     * @return java.math.BigDecimal
     */
    public BigDecimal toBigDecimal() {
        return new BigDecimal(toString());
    }

    public BigDecimal toBigDecimal(int precious, RoundingMode mode) {
        return new BigDecimal(toString(), new MathContext(precious, mode));
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-4-17 14:57:02)
     *
     * @return java.lang.Double
     */
    public Double toDouble() {
        return new Double(this.toString());
    }

    /**
     * 为了进行运算，简化运算，将符号位填加到每一个数值上去， 这样进行加后然后进行进位调整。 将所有的符号变换到每一位上
     *
     * @exception 异常描述
     * @see 需要参见的其它内容
     * @since 从类的那一个版本，此方法被添加进来。（可选）
     * @deprecated该方法从类的那一个版本后，已经被其它方法替
     */
    private void toPlus() {
        if (si == 1)
            return;
        si = 1;
        for (int i = 0; i < v.length; i++) {
            v[i] = -v[i];
        }
    }

    public String toString() {
        /** 没有添加位数，表示前面没有有效位数 */
        boolean addZero = false;
        StringBuffer sb = new StringBuffer();
        if (si == -1)
            sb.append("-");
        for (int i = v.length - 1; i > 0; i--) {
            if (v[i] == 0 && !addZero)
                continue;
            String temp = String.valueOf(v[i]);
            if (addZero) {
                int len = temp.length();
                int addZeroNo = EFFICIENCY_SEATE - len;
                for (int j = 0; j < addZeroNo; j++) {
                    sb.append('0');
                }
            }
            sb.append(temp);
            addZero = true;
        }
        if (!addZero)
            sb.append('0');
        /** 没有小数位 */
        if (power < 0) {
            sb.append('.');
            for (int j = 0; j < EFFICIENCY_SEATE && j < -power; j++) {
                sb.append((v[0] / POWER_ARRAY[j + 1]) % 10);
            }
        }
        // 压缩小数点后尾部0
        int index = -1;
        if (isTrimZero()) {
            if (power < 0) {
                String sTemp = sb.toString();
                for (int i = sb.length() - 1; i >= 0; i--) {
                    if (sTemp.substring(i, i + 1).equals("0"))
                        index = i;
                    else {
                        if (sTemp.substring(i, i + 1).equals(".")) {
                            index = i;
                        }
                        break;
                    }
                }
            }
        }
        if (index >= 0)
            sb = sb.delete(index, sb.length());
        return sb.toString();
    }

    public final static int DEFAULT_POWER = -8;

    private boolean trimZero = false;

    /**
     * 此处插入方法说明。 创建日期：(2001-11-14 10:37:15)
     *
     * @return nc.vo.pub.lang.UFDouble
     */
    public UFDouble abs() {
        UFDouble fdnew = new UFDouble();
        fdnew.power = this.power;
        fdnew.si = 1;
        for (int i = 0; i < v.length; i++) {
            fdnew.v[i] = v[i];
        }
        return fdnew;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-16 13:29:06)
     *
     * @return int
     */
    public int getPower() {
        return power;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-23 9:10:54)
     *
     * @return boolean
     */
    public boolean isTrimZero() {
        return trimZero;
    }

    /**
     * 此处插入方法说明。 创建日期：(2003-6-12 10:14:43)
     *
     * @param ufd
     *            nc.vo.pub.lang.UFDouble
     */
    public UFDouble mod(UFDouble ufd) {
        return mod(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }

    /**
     * 此处插入方法说明。 创建日期：(2003-6-12 10:14:43)
     *
     * @param ufd
     *            nc.vo.pub.lang.UFDouble
     */
    public UFDouble mod(UFDouble ufd, int newPower) {
        return mod(ufd, newPower, ROUND_HALF_UP);
    }

    /**
     * 此处插入方法说明。 创建日期：(2003-6-12 10:14:43)
     *
     * @param ufd
     *            nc.vo.pub.lang.UFDouble
     */
    public UFDouble mod(UFDouble ufd, int newPower, int roundingMode) {
        UFDouble ufdDiv = div(ufd, 0, ROUND_DOWN);
        UFDouble ufdnew = sub(ufdDiv.multiply(ufd));
        if (ufd.si != si)
            ufdnew = ufdnew.sub(ufd);
        if (ufdnew.si != si)
            ufdnew = ufdnew.sub(ufd);
        ufdnew.power = newPower;
        ufdnew.round(roundingMode);
        return ufdnew;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-23 9:10:54)
     *
     * @param newTrimZero
     *            boolean
     */
    public void setTrimZero(boolean newTrimZero) {
        trimZero = newTrimZero;
    }

    private static int getValidPower(int newPower) {
        /** power from 0 to -EFFICIENCY_SEATE */
        int power = newPower > 0 ? -newPower : newPower;
        if (power < -EFFICIENCY_SEATE)
            power = -EFFICIENCY_SEATE;
        return power;

    }

    @Override
    public int hashCode() {
        int v = 0;
        for (int i = 0; i < this.v.length; i++) {
            v += this.v[i];
        }
        return v * this.si;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UFDouble) {
            UFDouble ud = (UFDouble) o;
            return si == ud.si && Arrays.equals(v, ud.v);
        }

        return false;
    }

    public Object clone() {
        return new UFDouble(this);
    }
}