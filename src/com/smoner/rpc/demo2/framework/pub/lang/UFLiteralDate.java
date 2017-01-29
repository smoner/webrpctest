package com.smoner.rpc.demo2.framework.pub.lang;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 精度到天的日期类型，格式为yyyy-MM-dd，屏蔽了时区概念，无论在哪个时区，表现为同一时间
 *
 *
 */
public final class UFLiteralDate implements java.io.Serializable, Comparable<UFLiteralDate>, ICalendar {

    private static final long serialVersionUID = 1L;

    private static final int LRUSIZE = 500;

    private long utcTime;

    private transient GregorianCalendar basezoneCalendar;

    private final static Map<String, UFLiteralDate> allUsedDate1 = Collections.synchronizedMap(new LRUMap<String, UFLiteralDate>(
            512));

    private final static Map<Long, UFLiteralDate> allUsedDate2 = Collections.synchronizedMap(new LRUMap<Long, UFLiteralDate>(512));

    public static int num = 0;

    private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private static class LRUMap<K, V> extends LinkedHashMap<K, V> {

        private static final long serialVersionUID = 1L;

        public LRUMap(int initSize) {
            super(initSize, 1f, true);
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            if (size() > LRUSIZE)
                return true;
            else
                return false;
        }
    }

    public UFLiteralDate() {
        this(System.currentTimeMillis());
    }

    public UFLiteralDate(long m) {
        this.utcTime = m;
        utcTime = utcTime - utcTime % 1000;
    }

    /**
     * 用yyyy-MM-dd形式的字符串构造日期类型
     *
     * @param date
     */
    public UFLiteralDate(String date) {
        int[] v = internalParse(date);
        GregorianCalendar cal = new GregorianCalendar(BASE_TIMEZONE);
        cal.set(Calendar.YEAR, v[0]);
        cal.set(Calendar.MONTH, v[1] - 1);
        cal.set(Calendar.DATE, v[2]);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        utcTime = cal.getTimeInMillis();
        basezoneCalendar = cal;
    }

    /**
     * 比较日期先后，对象日期在参数日期之后为true
     */
    public boolean after(UFLiteralDate when) {
        return this.compareTo(when) > 0;
    }

    /**
     * 比较日期先后，精确到天
     *
     * @param when
     * @return
     */
    public boolean afterDate(UFLiteralDate when) {
        return compareTo(when) > 0 && !isSameDate(when);
    }

    /**
     * 比较日期先后，对象日期在参数日期之前为true
     */
    public boolean before(UFLiteralDate when) {
        return this.compareTo(when) < 0;
    }

    /**
     * 比较日期先后，精确到天
     *
     * @param when
     * @return
     */
    public boolean beforeDate(UFLiteralDate when) {
        return compareTo(when) < 0 && !isSameDate(when);
    }

    /**
     * 克隆日期对象。
     *
     * @return UFLiteralDate
     */
    public Object clone() {
        return new UFLiteralDate(utcTime);
    }

    /**
     * 返回日期先后： 大于0 ---为参数之后日期 等于0 ---和参数为同一天 小于0 ---为参数之前日期
     */
    public int compareTo(UFLiteralDate when) {
        long retl = this.utcTime - when.utcTime;
        if (retl == 0)
            return 0;
        else
            return retl > 0 ? 1 : -1;
    }

    /**
     * 比较日期先后，true为同一天
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return toString().equals(o.toString());
    }

    /**
     * 是否为同一天
     *
     * @param o
     * @return
     */
    public boolean isSameDate(UFLiteralDate o) {
        return equals(o);
    }

    /**
     * 返回天数后的日期
     *
     * @param days
     * @return
     */
    public UFLiteralDate getDateAfter(int days) {
        long l = utcTime + MILLIS_PER_DAY * days;
        return new UFLiteralDate(l);
    }

    /**
     * 返回天数前的日期
     *
     * @param days
     * @return
     */
    public UFLiteralDate getDateBefore(int days) {
        return getDateAfter(-days);
    }

    /**
     * 获取当前日期是多少号的数值
     *
     * @return
     */
    public int getDay() {
        return basezoneCalendar().get(Calendar.DATE);
    }

    /**
     * 获得年的数值
     *
     * @return int
     */
    public int getYear() {
        return basezoneCalendar().get(Calendar.YEAR);
    }

    /**
     * 返回某一日期距今天数，负数表示在今天之后
     *
     * @return int
     * @param when
     *
     */
    public int getDaysAfter(UFLiteralDate when) {
        int days = 0;
        if (when != null) {
            days = (int) ((this.utcTime - when.utcTime) / MILLIS_PER_DAY);
        }
        return days;
    }

    /**
     * 返回后一日期距前一日期之后后的天数
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getDaysBetween(UFLiteralDate begin, UFLiteralDate end) {
        if (begin != null && end != null) {
            return (int) ((end.utcTime - begin.utcTime) / MILLIS_PER_DAY);
        } else {
            throw new IllegalArgumentException("Dates to compare can't be null");
        }
    }

    public int getDaysMonth() {
        return getDaysMonth(getYear(), getMonth());
    }

    public String getEnMonth() {
        return MONTH_SYM[basezoneCalendar().get(Calendar.MONTH)];
    }

    public String getEnWeek() {
        return WEEK_SYM[getWeek()];
    }

    /**
     * 返回以1开始12结束的月
     *
     * @return
     */
    public int getMonth() {
        return basezoneCalendar().get(Calendar.MONTH) + 1;
    }

    /**
     * 返回开始0结束为6的当前日期的星期数
     *
     * @return
     */
    public int getWeek() {
        int days = getDaysAfter(new UFLiteralDate("1980-01-06"));
        int week = days % 7;
        if (week < 0)
            week += 7;
        return week;
    }

    /**
     * 返回MM格式月字符串
     *
     * @return
     */
    public String getStrMonth() {
        return toString().substring(5, 7);
    }

    /**
     * 返回dd格式日期字符串
     *
     * @return
     */
    public String getStrDay() {
        return toString().substring(8, 10);
    }

    /**
     * 是否闰年。
     *
     * @return boolean
     */
    public boolean isLeapYear() {
        return isLeapYear(getYear());
    }

    /**
     * 返加当前日期在一年内的周数。
     *
     * @return
     */
    public int getWeekOfYear() {
        return basezoneCalendar().get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据本地时间的时区返回日期
     *
     * @return
     */
    public Date toDate() {
        return new Date(utcTime);
    }

    /**
     * 返回yyyy-MM-dd格式字符串
     *
     * @return
     */
    public String toPersisted() {
        return toDateString(basezoneCalendar().get(Calendar.YEAR), basezoneCalendar.get(Calendar.MONTH) + 1,
                basezoneCalendar.get(Calendar.DATE));
    }

    /**
     * 返回yyyy-MM-dd格式字符串
     */
    public String toString() {
        return toPersisted();
    }

    public long getMillis() {
        return utcTime;
    }

    private GregorianCalendar basezoneCalendar() {
        if (basezoneCalendar == null) {
            basezoneCalendar = new GregorianCalendar(BASE_TIMEZONE);
            basezoneCalendar.setTimeInMillis(this.utcTime);
        }
        return basezoneCalendar;
    }

    /**
     * 用yyyy-MM-dd形式的字符串构造日期类型
     *
     * @param s
     *            日期字符串
     *
     * @return UFDate对象
     */
    public static UFLiteralDate fromPersisted(String s) {
        return getDate(s);
    }

    /**
     * 根据格林威治值获取日期
     *
     * @param d
     * @return
     */
    public static UFLiteralDate getDate(long d) {
        if (rwl.readLock().tryLock()) {
            try {
                long longDate = d - d % 1000;
                UFLiteralDate o = (UFLiteralDate) allUsedDate2.get(longDate);
                if (o == null) {
                    UFLiteralDate n = new UFLiteralDate(longDate);
                    rwl.readLock().unlock();
                    rwl.writeLock().lock();
                    try {
                        o = n;
                        allUsedDate2.put(longDate, o);
                    } finally {
                        rwl.readLock().lock();
                        rwl.writeLock().unlock();
                    }
                }
                return o;
            } finally {
                rwl.readLock().unlock();
            }
        } else {
            return new UFLiteralDate(d);
        }
    }

    /**
     * 根据字符串获取时间
     *
     * @param strDate
     * @return
     */
    public static UFLiteralDate getDate(String strDate) {
        if (rwl.readLock().tryLock()) {
            try {
                UFLiteralDate o = (UFLiteralDate) allUsedDate1.get(strDate);
                if (o == null) {
                    UFLiteralDate n = new UFLiteralDate(strDate);
                    rwl.readLock().unlock();
                    rwl.writeLock().lock();
                    try {
                        o = n;
                        allUsedDate1.put(strDate, o);
                    } finally {
                        rwl.readLock().lock();
                        rwl.writeLock().unlock();
                    }
                }
                return o;
            } finally {
                rwl.readLock().unlock();
            }
        } else {
            return new UFLiteralDate(strDate);
        }
    }

    /**
     * 根据Date获取日期
     *
     * @param date
     * @return
     */
    public static UFLiteralDate getDate(Date date) {
        return new UFLiteralDate(date);
    }

    static int[] internalParse(String str) {
        if (str == null)
            throw new IllegalArgumentException("invalid date: " + str);

        str = str.trim();
        int spaceIndex = str.indexOf(' ');
        if (spaceIndex > -1) {
            str = str.substring(0, spaceIndex);
        }

        String[] tokens = new String[3];
        StringTokenizer st = new StringTokenizer(str, "-/");
        if (st.countTokens() != 3) {
            throw new IllegalArgumentException("invalid date: " + str);
        }

        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i++] = st.nextToken().trim();
        }

        try {
            int year = Integer.parseInt(tokens[0]);
            int month = Integer.parseInt(tokens[1]);
            if (month < 1 || month > 12)
                throw new IllegalArgumentException("invalid date: " + str);
            int day = Integer.parseInt(tokens[2]);

            int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1] : MONTH_LENGTH[month - 1];

            if (day < 1 || day > daymax)
                throw new IllegalArgumentException("invalid date: " + str);
            return new int[] { year, month, day };
        } catch (Throwable thr) {
            if (thr instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) thr;
            } else {
                throw new IllegalArgumentException("invalid date: " + str);
            }
        }

    }

    /**
     * 返回指定年月的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDaysMonth(int year, int month) {
        if (isLeapYear(year)) {
            return LEAP_MONTH_LENGTH[month - 1];
        } else {
            return MONTH_LENGTH[month - 1];
        }
    }

    /**
     * 是否闰年。
     *
     * @return boolean
     * @param year
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
            return true;
        else
            return false;
    }

    private static String toDateString(int year, int month, int day) {
        String strYear = String.valueOf(year);
        for (int j = strYear.length(); j < 4; j++)
            strYear = "0" + strYear;
        String strMonth = String.valueOf(month);
        if (strMonth.length() < 2)
            strMonth = "0" + strMonth;
        String strDay = String.valueOf(day);
        if (strDay.length() < 2)
            strDay = "0" + strDay;
        return strYear + "-" + strMonth + "-" + strDay;
    }

    @Override
    public int hashCode() {
        return (int) (utcTime ^ (utcTime >>> 32));
    }

    // 以下API为实现接口所必须， 不建议使用

    public UFLiteralDate(java.sql.Date date) {
        this(date.getTime());
    }

    public UFLiteralDate(java.util.Date date) {
        this(date.getTime());
    }

    public String toStdString(TimeZone zone) {
        return toPersisted();
    }

    public String toString(TimeZone zone, DateFormat format) {
        zone = Calendars.getGMTTimeZone(BASE_TIMEZONE);
        Date dt = new Date(utcTime);
        format.setTimeZone(zone);
        return format.format(dt);
    }

    public String toStdString() {
        return toPersisted();
    }

}