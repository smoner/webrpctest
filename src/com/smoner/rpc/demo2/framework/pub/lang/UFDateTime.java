package com.smoner.rpc.demo2.framework.pub.lang;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 *
 */
public final class UFDateTime implements java.io.Serializable,
        Comparable<UFDateTime>, ICalendar {
    static final long serialVersionUID = 1L;

    static {
        Calendars.getGMTDefault();
    }

    private long utcTime;

    /**
     * UFTime 构造子注
     */
    public UFDateTime() {
        this(System.currentTimeMillis());
    }

    /**
     * 以从1970年1月1日0时0分0秒到现在的毫秒数来构造日期和时间
     *
     * @param m
     */
    public UFDateTime(long m) {
        this.utcTime = m;
        utcTime = utcTime - utcTime % 1000;
    }

    /**
     * 用字符串yyyy-mm-dd hh:mm:ss格式表示日期时间
     *
     * @param date
     * @throws
     */
    public UFDateTime(String date) {
        int[] t = internalParse(date);
        this.utcTime = new GregorianCalendar(t[0], t[1] - 1, t[2], t[3], t[4],
                t[5]).getTimeInMillis();
    }

    public UFDateTime(String date, TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        int[] t = internalParse(date);
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.set(Calendar.YEAR, t[0]);
        cal.set(Calendar.MONTH, t[1] - 1);
        cal.set(Calendar.DATE, t[2]);
        cal.set(Calendar.HOUR_OF_DAY, t[3]);
        cal.set(Calendar.MINUTE, t[4]);
        cal.set(Calendar.SECOND, t[5]);
        cal.set(Calendar.MILLISECOND, 0);
        utcTime = cal.getTimeInMillis();
    }

    public UFDateTime(java.sql.Date date) {
        this((java.util.Date) date);
    }

    public UFDateTime(java.util.Date date) {
        this(date.getTime());
    }

    /**
     * 通过UFDate及UFTime构造函数 如果UFDate为空，那么设置为当前日期 如果UFTime为空，那么设置为当前时间
     *
     * @param date
     * @param time
     */
    public UFDateTime(UFDate date, UFTime time) {
        if (null == date) {
            date = new UFDate(true);
        }

        if (null == time) {
            time = new UFTime();
        }
        this.utcTime = Calendars.getMillis(date, time,
                Calendars.getGMTDefault());
    }

    /**
     * 比较日期时间先后，true为之后
     *
     * @param dateTime
     * @return
     */
    public boolean after(UFDateTime dateTime) {
        return this.utcTime - dateTime.utcTime > 0;
    }

    /**
     * 比较日期时间先后，true为之前
     *
     * @param dateTime
     * @return
     */
    public boolean before(UFDateTime dateTime) {
        return this.utcTime - dateTime.utcTime < 0;
    }

    public Object clone() {
        return new UFDateTime(utcTime);
    }

    /**
     * 返回日期先后，大于0为之后，等于0为同一日期时间，小于0为之前
     */
    public int compareTo(UFDateTime dateTime) {
        long ret = this.utcTime - dateTime.utcTime;
        if (ret == 0)
            return 0;
        return ret > 0 ? 1 : -1;
    }

    /**
     * 返回日期
     *
     * @return
     */
    public UFDate getDate() {
        return new UFDate(this.utcTime);
    }

    /**
     * 返回一天起始时间
     *
     * @return
     */
    public UFDate getBeginDate() {
        return new UFDate(this.utcTime).asBegin();
    }

    /**
     * 返回一天的结束时间
     *
     * @return
     */
    public UFDate getEndDate() {
        return new UFDate(this.utcTime).asEnd();
    }

    /**
     * 比较日期先后，true为之后
     *
     * @param when
     * @return
     */
    public boolean after(UFDate when) {
        return getDate().compareTo(when) > 0;
    }

    public boolean afterDate(UFDate when) {
        return getDate().afterDate(when);
    }

    /**
     * 比较日期先后，true为之前
     *
     * @param when
     * @return
     */
    public boolean before(UFDate when) {
        return getDate().compareTo(when) < 0;
    }

    /**
     * 比较日期先后, 精确到天
     *
     * @param when
     * @return
     */
    public boolean beforeDate(UFDate when) {
        return getDate().beforeDate(when);
    }

    /**
     * 返回日期先后，大于0为之后，等于0为同一日期，小于0为之前
     *
     * @param when
     * @return
     */
    public int dateCompare(UFDate when) {
        return getDate().compareTo(when);
    }

    /**
     * 比较日期先后，true为同一天
     */
    public boolean isSameDate(UFDate when) {
        return getDate().isSameDate(when);
    }

    /**
     * 比较日期先后，指定时区
     *
     * @param when
     * @param zone
     * @return
     */
    public boolean isSameDate(UFDate when, TimeZone zone) {
        return getDate().isSameDate(when, zone);
    }

    /**
     * 返回某一日期距今天数，负数表示在今天之后
     *
     * @param when
     * @return
     */
    public int getDaysAfter(UFDate when) {
        if (when != null) {
            return (int) ((utcTime - when.getMillis()) / MILLIS_PER_DAY);
        }
        return 0;
    }

    /**
     * 返回天数后的日期时间，时间不变
     *
     * @param days
     * @return
     */
    public UFDateTime getDateTimeAfter(int days) {
        return new UFDateTime(this.utcTime + MILLIS_PER_DAY * days);
    }

    /**
     * 返回天数前的日期时间，时间不变
     *
     * @param days
     * @return
     */
    public UFDateTime getDateTimeBefore(int days) {
        return getDateTimeAfter(-days);
    }

    /**
     * 返回天
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
     * 返回某一日期距今天数，负数表示在今天之前,忽略时间
     *
     * @param when
     * @return
     */
    public int getDaysAfter(UFDateTime when) {
        int days = 0;
        if (when != null) {
            days = (int) ((utcTime - when.getMillis()) / MILLIS_PER_DAY);
        }
        return days;
    }

    /**
     * 返回后一日期距前一日期之后后的天数,不考虑时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getDaysBetween(UFDate begin, UFDate end) {
        if (begin != null && end != null) {
            return (int) ((end.getMillis() - begin.getMillis()) / MILLIS_PER_DAY);
        }
        return 0;
    }

    /**
     * 返回后一日期距前一日期之后后的小时数
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getHoursBetween(UFDateTime begin, UFDateTime end) {
        return (int) (getMilisBetween(begin, end) / MILLIS_PER_HOUR);
    }

    /**
     * 返回后一日期距前一日期之后后的分钟数
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getMinutesBetween(UFDateTime begin, UFDateTime end) {
        return (int) (getMilisBetween(begin, end) / MILLIS_PER_MINUTE);
    }

    /**
     * 返回后一日期距前一日期之后后的秒数
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getSecondsBetween(UFDateTime begin, UFDateTime end) {
        return (int) (getMilisBetween(begin, end) / MILLIS_PER_SECOND);
    }

    /**
     * 取得两个UFDate之间相差的毫秒数
     *
     * @param begin
     * @param end
     * @return
     */
    private static long getMilisBetween(UFDateTime begin, UFDateTime end) {
        return end.utcTime - begin.utcTime;
    }

    /**
     * 返回后一日期距前一日期之间的天数,不考虑时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getDaysBetween(UFDate begin, UFDateTime end) {
        return getDaysBetween(begin, end.getDate());
    }

    /**
     * 返回后一日期距前一日期之间的天数,不考虑时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getDaysBetween(UFDateTime begin, UFDate end) {
        return getDaysBetween(begin.getDate(), end);
    }

    /**
     * 返回后一日期距前一日期之间的天数,不考虑时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static int getDaysBetween(UFDateTime begin, UFDateTime end) {
        return getDaysBetween(begin.getDate(), end.getDate());
    }

    /**
     * 返回当前月的天数
     *
     * @return
     */
    public int getDaysMonth() {
        GregorianCalendar baseCal = basezoneCalendar();
        return getDaysMonth(baseCal.get(Calendar.YEAR),
                baseCal.get(Calendar.MONTH) + 1);
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
     * 返回当前月的英文符号
     *
     * @return
     */
    public String getEnMonth() {
        return MONTH_SYM[basezoneCalendar().get(Calendar.MONTH)];
    }

    /**
     * 返回当周的英文符号
     *
     * @return
     */
    public String getEnWeek() {
        return WEEK_SYM[getWeek()];
    }

    /**
     * 返回当周(0-6)
     *
     * @return
     */
    public int getWeek() {
        return basezoneCalendar().get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获得年的数值
     *
     * @return
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
     * 返回当前月(1-12)
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
     * 返回当前24时制时间
     *
     * @return
     */
    public int getHour() {
        return basezoneCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public int getLocalHour() {
        return localCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public int getHout(TimeZone zone) {
        return getCalendar(zone).get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回当前分钟
     *
     * @return
     */
    public int getMinute() {
        return basezoneCalendar().get(Calendar.MINUTE);
    }

    public int getLocalMinute() {
        return localCalendar().get(Calendar.MINUTE);
    }

    public int getMinute(TimeZone zone) {
        return getCalendar(zone).get(Calendar.MINUTE);
    }

    /**
     * 返回当前秒
     *
     * @return
     */
    public int getSecond() {
        return basezoneCalendar().get(Calendar.SECOND);
    }

    /**
     * 得到GMT毫秒数(实际精确到秒级)
     *
     * @return
     */
    public long getMillis() {
        return utcTime;
    }

    /**
     * 计算当前时间落后指定时间的毫秒数（实际精确到秒级)
     *
     * @param dateTime
     * @return
     */
    public long getMillisAfter(UFDateTime dateTime) {
        if (dateTime != null) {
            return this.utcTime - dateTime.utcTime;
        } else {
            throw new IllegalArgumentException("date time can't be null");
        }
    }

    /**
     * 返回MM格式月字符串
     *
     * @return
     */
    public String getStrMonth() {
        int month = getMonth();
        if (month > 0 && month < 10)
            return "0" + Integer.toString(month);
        else if (month >= 10 && month < 13)
            return Integer.toString(month);
        else
            return null;
    }

    /**
     * 返回dd格式日期字符串
     *
     * @return
     */
    public String getStrDay() {
        int day = getDay();
        if (day > 0 && day < 10)
            return "0" + Integer.toString(day);
        else if (day >= 10 && day < 32)
            return Integer.toString(day);
        else
            return null;
    }

    /**
     * 返回HH:mm:dd格式时间字符串
     *
     * @return
     */
    public String getTime() {
        return toString().substring(11, 19);
    }

    /**
     * 转化为UFTime
     *
     * @return
     */
    public UFTime getUFTime() {
        return new UFTime(utcTime);
    }

    /**
     * 是否闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
            return true;
        else
            return false;
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
     * 根据基准时区，以yyyy-MM-dd HH:mm:ss的标准格式转化字符串
     */
    public String toString() {
        GregorianCalendar baseCalendar = basezoneCalendar();
        return toDateTimeString(baseCalendar.get(Calendar.YEAR),
                baseCalendar.get(Calendar.MONTH) + 1,
                baseCalendar.get(Calendar.DATE),
                baseCalendar.get(Calendar.HOUR_OF_DAY),
                baseCalendar.get(Calendar.MINUTE),
                baseCalendar.get(Calendar.SECOND));
    }

    public String toLocalString() {
        GregorianCalendar localCalendar = localCalendar();
        return toDateTimeString(localCalendar.get(Calendar.YEAR),
                localCalendar.get(Calendar.MONTH) + 1,
                localCalendar.get(Calendar.DATE),
                localCalendar.get(Calendar.HOUR_OF_DAY),
                localCalendar.get(Calendar.MINUTE),
                localCalendar.get(Calendar.SECOND));
    }

    public String toString(TimeZone zone, DateFormat format) {
        zone = Calendars.getGMTTimeZone(zone);
        Date dt = new Date(utcTime);
        format.setTimeZone(zone);
        return format.format(dt);
    }

    /**
     * 转化为标准时间格式的字符串
     *
     * @return
     */
    public String toStdString() {
        return toStdString(BASE_TIMEZONE);
    }

    public String toStdString(TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        if (zone.equals(Calendars.getGMTDefault())) {
            return toString();
        }
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.setTimeInMillis(utcTime);
        return toDateTimeString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
    }

    /**
     * 比较日期时间先后，true为同一日期时间
     */
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof UFDateTime)) {
            return this.utcTime == ((UFDateTime) o).utcTime;
        }
        return false;
    }

    public int hashCode() {
        return (int) (utcTime ^ (utcTime >>> 32));
    }

    private GregorianCalendar localCalendar() {
        GregorianCalendar localCalendar = new GregorianCalendar(
                Calendars.getGMTDefault());
        localCalendar.setTimeInMillis(this.utcTime);
        return localCalendar;
    }

    private GregorianCalendar basezoneCalendar() {
        GregorianCalendar basezoneCalendar = new GregorianCalendar(
                BASE_TIMEZONE);
        basezoneCalendar.setTimeInMillis(this.utcTime);
        return basezoneCalendar;
    }

    private GregorianCalendar getCalendar(TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        GregorianCalendar localCalendar = new GregorianCalendar(zone);
        localCalendar.setTimeInMillis(this.utcTime);
        return localCalendar;
    }

    static String toDateTimeString(int year, int month, int day, int hour,
                                   int minute, int second) {
        StringBuffer sb = new StringBuffer();
        String strYear = String.valueOf(year);
        for (int j = strYear.length(); j < 4; j++)
            sb.append('0');
        sb.append(strYear).append('-');

        append(sb, month, '-');
        append(sb, day, ' ');
        append(sb, hour, ':');
        append(sb, minute, ':');
        if (second < 10) {
            sb.append('0');
        }
        sb.append(second);
        return sb.toString();
    }

    private static void append(StringBuffer sb, int v, char split) {
        if (v < 10) {
            sb.append('0');
        }
        sb.append(v).append(split);
    }

    static int[] internalParse(String str) {
        if (str == null) {
            throw new IllegalArgumentException("invalid UFDateTime: " + str);
        }
        str = str.trim();
        int index = str.indexOf(' ');
        if (index < 0 || index > (str.length() - 1)) {
            throw new IllegalArgumentException("invalid UFDateTime: " + str);
        }
        int[] d = UFDate.internalParse(str);

        int[] t = UFTime.internalParse(str, index + 1);

        int[] a = new int[6];

        System.arraycopy(d, 0, a, 0, d.length);

        System.arraycopy(t, 0, a, d.length, t.length);

        return a;

    }

    public String toPersisted() {
        return toStdString();
    }
}