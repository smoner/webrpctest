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
 * 精度到天的日期类型，标准格式为yyyy-MM-dd
 *
 *
 */
public final class UFDate implements java.io.Serializable, Comparable<UFDate>, ICalendar {

    private static final long serialVersionUID = 1L;

    private static final int LRUSIZE = 500;

    private long utcTime;

    private final static Map<String, UFDate> allUsedDate1 = Collections.synchronizedMap(new LRUMap<String, UFDate>(512));
    public static int num = 0;

    private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    static {
        Calendars.getGMTDefault();
    }

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

    public UFDate() {
        this(System.currentTimeMillis());
    }

    public UFDate(long m) {
        this.utcTime = m;
        utcTime = utcTime - utcTime % 1000;
    }

    public UFDate(java.sql.Date date) {
        this(date.getTime());
    }

    public UFDate(java.util.Date date) {
        this(date.getTime());
    }

    /**
     * 用当前时间构造日期类型，所使用的时区为本地时间所处时区，指明是否是开始时间
     *
     * @param begin
     *            如果为true,则为0时0分0秒；否则为23时59分59秒
     */
    public UFDate(boolean begin) {
        GregorianCalendar cal = new GregorianCalendar(Calendars.getGMTDefault());
        cal.setTimeInMillis(System.currentTimeMillis());
        if (begin) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 0);
        }
        this.utcTime = cal.getTimeInMillis();
    }

    /**
     * 用yyyy-MM-dd形式的字符串构造日期类型，所使用的时区为本地时间所处时区，指明是否是开始时间
     *
     * @param date
     * @param begin
     *            如果为true,则为0时0分0秒；否则为23时59分59秒
     */
    public UFDate(String date, boolean begin) {
        int[] v = internalParse(date);
        GregorianCalendar cal = null;
        if (begin) {
            cal = new GregorianCalendar(v[0], v[1] - 1, v[2]);
        } else {
            cal = new GregorianCalendar(v[0], v[1] - 1, v[2], 23, 59, 59);
        }
        utcTime = cal.getTimeInMillis();
    }

    /**
     * 用yyyy-MM-dd形式的字符串构造日期类型，所使用的时区为本地时间所处时区
     *
     * @param date
     */
    public UFDate(String date) {
        int[] v = internalParse(date);
        utcTime = new GregorianCalendar(v[0], v[1] - 1, v[2]).getTimeInMillis();
    }

    /**
     * 用yyyy-MM-dd形式的字符串构造日期类型，所使用的时区为传入时区对应的UTC(GMT)时区，指明是否是开始时间
     *
     * @param date
     *            日期字符串
     * @param zone
     *            时区
     * @param begin
     *            如果为true,则为0时0分0秒；否则为23时59分59秒
     */
    public UFDate(String date, TimeZone zone, boolean begin) {
        zone = Calendars.getGMTTimeZone(zone);
        int[] v = internalParse(date);
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.set(Calendar.YEAR, v[0]);
        cal.set(Calendar.MONTH, v[1] - 1);
        cal.set(Calendar.DATE, v[2]);
        if (begin) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 0);
        }
        this.utcTime = cal.getTimeInMillis();
    }

    /**
     * 用yyyy-MM-dd形式的字符串构造日期类型，所使用的时区为传入时区对应的UTC(GMT)时区
     *
     * @param date
     * @param zone
     *
     */
    public UFDate(String date, TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        int[] v = internalParse(date);
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.set(Calendar.YEAR, v[0]);
        cal.set(Calendar.MONTH, v[1] - 1);
        cal.set(Calendar.DATE, v[2]);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        utcTime = cal.getTimeInMillis();
    }

    /**
     * 比较日期先后，对象日期在参数日期之后为true
     */
    public boolean after(UFDate when) {
        return this.compareTo(when) > 0;
    }

    /**
     * 比较日期先后，精确到天
     *
     * @param when
     * @return
     */
    public boolean afterDate(UFDate when) {
        return compareTo(when) > 0 && !isSameDate(when);
    }

    /**
     * 比较日期先后，对象日期在参数日期之前为true
     */
    public boolean before(UFDate when) {
        return this.compareTo(when) < 0;
    }

    /**
     * 比较日期先后，精确到天
     *
     * @param when
     * @return
     */
    public boolean beforeDate(UFDate when) {
        return compareTo(when) < 0 && !isSameDate(when);
    }

    /**
     * 克隆日期兑对象。
     *
     * @return nc.vo.pub.lang.UFDate
     */
    public Object clone() {
        return new UFDate(utcTime);
    }

    /**
     * 返回日期先后： 大于0 ---为参数之后日期 等于0 ---和参数为同一天 小于0 ---为参数之前日期
     */
    public int compareTo(UFDate when) {
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
        if ((o != null) && (o instanceof UFDate)) {
            return this.utcTime == ((UFDate) o).utcTime;
        }
        return false;
    }

    /**
     * 是否为同一天，基准时区
     *
     * @param o
     * @return
     */
    public boolean isSameDate(UFDate o) {
        GregorianCalendar cal = new GregorianCalendar(BASE_TIMEZONE);
        cal.setTimeInMillis(o.getMillis());
        GregorianCalendar basezoneCalendar = basezoneCalendar();
        if (basezoneCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                && basezoneCalendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                && basezoneCalendar.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否为同一天，指定时区
     *
     * @param o
     * @param zone
     * @return
     */
    public boolean isSameDate(UFDate o, TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.setTimeInMillis(o.getMillis());
        GregorianCalendar cal1 = getCalendar(zone);
        if (cal1.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                && cal1.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
            return true;
        } else {
            return false;
        }
    }

    public static UFDate getDate(long d) {
        d = d - d % 1000;
        return getDate(Long.valueOf(d));
    }

    public static UFDate getDate(String strDate) {
        return new UFDate(strDate);
    }

    public static UFDate getDate(String strDate, TimeZone zone, boolean begin) {
        if (rwl.readLock().tryLock()) {
            try {
                String key = strDate + zone.getID() + begin;
                UFDate o = (UFDate) allUsedDate1.get(key);
                if (o == null) {
                    UFDate n = new UFDate(strDate, zone, begin);
                    rwl.readLock().unlock();
                    rwl.writeLock().lock();
                    try {
                        o = n;
                        allUsedDate1.put(key, o);
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
            return new UFDate(strDate, zone, begin);
        }

    }

    public static UFDate getDate(Date date) {
        return new UFDate(date);
    }

    public static UFDate getDate(Long date) {
        return new UFDate(date);
    }

    /**
     * 基准时区起始时间
     *
     * @return
     */
    public UFDate asBegin() {
        return asBegin(BASE_TIMEZONE);
    }

    /**
     * 指定时区起始时间
     *
     * @param zone
     * @return
     */
    public UFDate asBegin(TimeZone zone) {
        GregorianCalendar calendar = getCalendar(zone);
        if (0 == calendar.get(Calendar.HOUR_OF_DAY) && 0 == calendar.get(Calendar.MINUTE) && 0 == calendar.get(Calendar.SECOND)) {
            return this;
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return new UFDate(calendar.getTimeInMillis());
    }

    /**
     * 本地时区起始时间
     *
     * @return
     */
    public UFDate asLocalBegin() {
        return asBegin(Calendars.getGMTDefault());
    }

    /**
     * 基准时区结束时间
     *
     * @return
     */
    public UFDate asEnd() {
        return asEnd(BASE_TIMEZONE);
    }

    /**
     * 指定时区结束时间
     *
     * @param zone
     * @return
     */
    public UFDate asEnd(TimeZone zone) {
        GregorianCalendar calendar = getCalendar(zone);
        if (23 == calendar.get(Calendar.HOUR_OF_DAY) && 59 == calendar.get(Calendar.MINUTE)
                && 59 == calendar.get(Calendar.SECOND)) {
            return this;
        }
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return new UFDate(calendar.getTimeInMillis());
    }

    /**
     * 本地时区结束时间
     *
     * @return
     */
    public UFDate asLocalEnd() {
        return asEnd(Calendars.getGMTDefault());
    }

    /**
     * 返回天数后的日期
     *
     * @param days
     * @return
     */
    public UFDate getDateAfter(int days) {
        long l = utcTime + MILLIS_PER_DAY * days;
        return new UFDate(l);
    }

    /**
     * 返回天数前的日期
     *
     * @param days
     * @return
     */
    public UFDate getDateBefore(int days) {
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

    public int getLocalDay() {
        return localCalendar().get(Calendar.DATE);
    }

    public int getDay(TimeZone zone) {
        return getCalendar(zone).get(Calendar.DATE);
    }

    /**
     * 获得年的数值
     *
     * @return int
     */
    public int getYear() {
        return basezoneCalendar().get(Calendar.YEAR);
    }

    public int getLocalYear() {
        return localCalendar().get(Calendar.YEAR);
    }

    public int getYear(TimeZone zone) {
        return getCalendar(zone).get(Calendar.YEAR);
    }

    /**
     * 返回某一日期距今天数，负数表示在今天之后
     *
     * @return int
     * @param when
     *
     */
    public int getDaysAfter(UFDate when) {
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
    public static int getDaysBetween(UFDate begin, UFDate end) {
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

    public int getLocalMonth() {
        return localCalendar().get(Calendar.MONDAY) + 1;
    }

    public int getMonth(TimeZone zone) {
        return getCalendar(zone).get(Calendar.MONDAY) + 1;
    }

    /**
     * 返回开始0结束为6的当前日期的星期数
     *
     * @return
     */
    public int getWeek() {
        int days = getDaysAfter(new UFDate("1980-01-06"));
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
     * 转化成无时区概念的UFDate
     */
    public UFLiteralDate toUFLiteralDate(TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        return new UFLiteralDate(toStdString(zone));
    }

    public int hashCode() {
        return (int) (utcTime ^ (utcTime >>> 32));
    }

    /**
     * 根据基准时区，当标准格式yyyy-MM-dd HH:mm:ss返回字符串
     *
     * @return
     */
    public String toPersisted() {
        GregorianCalendar cal = new GregorianCalendar(BASE_TIMEZONE);
        cal.setTimeInMillis(utcTime);
        return UFDateTime.toDateTimeString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }

    /**
     * 用yyyy-MM-dd HH:mm:ss形式的字符串构造日期类型，所使用的时区为基准时区
     *
     * @param s
     *            日期字符串
     * @return UFDate对象
     */
    public static UFDate fromPersisted(String s) {
        return new UFDateTime(s, ICalendar.BASE_TIMEZONE).getDate();
    }

    /**
     * 根据基准时区,以标准格式yyyy-MM-dd返回字符串
     *
     * @return
     */
    public String toStdString() {
        return toStdString(BASE_TIMEZONE);
    }

    /**
     * 根据指定时区,以标准格式yyyy-MM-dd返回字符串
     *
     * @param zone
     * @return
     */
    public String toStdString(TimeZone zone) {
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.setTimeInMillis(utcTime);
        return toDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));

    }

    public String toString(TimeZone zone, DateFormat format) {
        zone = Calendars.getGMTTimeZone(zone);
        Date dt = new Date(utcTime);
        format.setTimeZone(zone);
        return format.format(dt);
    }

    /**
     * 根据基准时区，以yyyy-MM-dd HH:mm:ss的标准格式转化字符串
     */
    public String toString() {
        return toPersisted();
    }

    /**
     * 根据本地时区，以yyyy-MM-dd的标准格式转化字符串
     *
     * @return
     */
    public String toLocalString() {
        GregorianCalendar localCalendar = localCalendar();
        return toDateString(localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH) + 1,
                localCalendar.get(Calendar.DATE));
    }

    public long getMillis() {
        return utcTime;
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
            tokens[i++] = st.nextToken();
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
     *            int
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
            return true;
        else
            return false;
    }

    private GregorianCalendar localCalendar() {
        GregorianCalendar localCalendar = new GregorianCalendar(Calendars.getGMTDefault());
        localCalendar.setTimeInMillis(this.utcTime);
        return localCalendar;
    }

    private GregorianCalendar basezoneCalendar() {
        GregorianCalendar basezoneCalendar = new GregorianCalendar(BASE_TIMEZONE);
        basezoneCalendar.setTimeInMillis(this.utcTime);
        return basezoneCalendar;
    }

    private GregorianCalendar getCalendar(TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        GregorianCalendar basezoneCalendar = new GregorianCalendar(zone);
        basezoneCalendar.setTimeInMillis(this.utcTime);
        return basezoneCalendar;
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

}