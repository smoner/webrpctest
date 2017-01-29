package com.smoner.rpc.demo2.framework.pub.lang;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
public final class UFTime implements java.io.Serializable, Comparable<UFTime>, ICalendar {

    static {
        Calendars.getGMTDefault();
    }
    private static final long serialVersionUID = -7719973293202240085L;

    private long utcOffset;

    public UFTime() {
        this(System.currentTimeMillis());
    }

    public UFTime(long m) {
        utcOffset = m % MILLIS_PER_DAY;
        utcOffset = utcOffset - utcOffset % 1000;
        int rawOffset = Calendars.getGMTDefault().getRawOffset();
        // add by liumiao 2014-7-10 应该以绝对值方式计算
        if (rawOffset < 0) {
            if (utcOffset < rawOffset) {
                utcOffset += ICalendar.MILLIS_PER_DAY;
            }
            // else if (utcOffset >= ICalendar.MILLIS_PER_DAY - rawOffset) {
            // utcOffset -= ICalendar.MILLIS_PER_DAY;
            // }
        } else if (rawOffset > 0) {
            if (utcOffset >= ICalendar.MILLIS_PER_DAY - rawOffset) {
                utcOffset -= ICalendar.MILLIS_PER_DAY;
            }
        }
    }

    public UFTime(String time, TimeZone zone) {
        // zone = Calendars.getGMTTimeZone(zone);
        // for literal
        zone = Calendars.getGMTTimeZone(BASE_TIMEZONE);
        int[] t = internalParse(time);
        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, t[0]);
        cal.set(Calendar.MINUTE, t[1]);
        cal.set(Calendar.SECOND, t[2]);
        cal.set(Calendar.MILLISECOND, 0);
        utcOffset = cal.getTimeInMillis();
    }

    public UFTime(String time) {
        int[] t = internalParse(time);
        // utcOffset = ((t[0] * 60 + t[1]) * 60 + t[2]) * 1000
        // - TimeZone.getDefault().getRawOffset();
        utcOffset = ((t[0] * 60 + t[1]) * 60 + t[2]) * 1000 - Calendars.getGMTTimeZone(BASE_TIMEZONE).getRawOffset();
        utcOffset = utcOffset - utcOffset % 1000;
    }

    public UFTime(java.sql.Date date) {
        this((java.util.Date) date);
    }

    public UFTime(java.util.Date date) {
        this(date.getTime());
    }

    public String toStdString(TimeZone zone) {
        // for literal
        zone = Calendars.getGMTTimeZone(BASE_TIMEZONE);
        long time = (zone.getRawOffset() + utcOffset) % MILLIS_PER_DAY;
        if (time < 0) {
            time = MILLIS_PER_DAY + time;
        }

        long as = time / MILLIS_PER_SECOND;
        int second = (int) (as % 60);
        long am = (as - second) / 60;
        int minute = (int) (am % 60);
        int hour = (int) (am - minute) / 60;
        StringBuffer sb = new StringBuffer();
        append(sb, hour, ':');
        append(sb, minute, ':');
        if (second < 10) {
            sb.append('0');
        }
        sb.append(second);
        return sb.toString();
    }

    public String toStdString() {
        return toStdString(BASE_TIMEZONE);
    }

    public String toString() {
        return toStdString(BASE_TIMEZONE);
    }

    /**
     * 根据本地时区，返回HH:mm:ss格式字符串
     *
     * @return
     */
    public String toLocalString() {
        return toStdString(Calendars.getGMTDefault());
    }

    /**
     * 比较时间先后，对象时间在参数时间之后为true
     */
    public boolean after(UFTime when) {
        return utcOffset - when.utcOffset > 0;
    }

    /**
     * 比较时间先后，对象时间在参数时间之前为true，不考虑日期
     */
    public boolean before(UFTime when) {
        return utcOffset - when.utcOffset < 0;
    }

    /**
     * 克隆时间对象。
     *
     * @return
     */
    public Object clone() {
        return new UFTime(utcOffset);
    }

    public boolean equals(Object o) {
        if ((o != null) && (o instanceof UFTime)) {
            return this.utcOffset == ((UFTime) o).utcOffset;
        }
        return false;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + (int) utcOffset;
        return hash;
    }

    public int getHour() {
        return Integer.valueOf(toString().substring(0, 2)).intValue();
    }

    public int getLocalHour() {
        return Integer.valueOf(toStdString(Calendars.getGMTDefault()).substring(0, 2)).intValue();
    }

    public int getHour(TimeZone zone) {
        return Integer.valueOf(toStdString(Calendars.getGMTTimeZone(zone)).substring(0, 2)).intValue();
    }

    public int getMinute() {
        return Integer.valueOf(toString().substring(3, 5)).intValue();
    }

    public int getLocalMinute() {
        return Integer.valueOf(toStdString(Calendars.getGMTDefault()).substring(3, 5)).intValue();
    }

    public int getMinute(TimeZone zone) {
        return Integer.valueOf(toStdString(Calendars.getGMTTimeZone(zone)).substring(3, 5)).intValue();
    }

    public int getSecond() {
        return Integer.valueOf(toString().substring(6, 8)).intValue();
    }

    /**
     * 得到GMT毫秒数
     *
     * @return
     */
    public long getMillis() {
        return utcOffset;
    }

    public int compareTo(UFTime o) {
        long ret = utcOffset - o.utcOffset;
        if (ret == 0) {
            return 0;
        }
        return ret > 0 ? 1 : -1;
    }

    private static void append(StringBuffer sb, int v, char split) {
        if (v < 10) {
            sb.append('0');
        }
        sb.append(v).append(split);
    }

    static int[] internalParse(String time) {
        return internalParse(time.trim(), 0);
    }

    static int[] internalParse(String time, int idx) {
        if (StringUtil.isEmpty(time)) {
            throw new IllegalArgumentException("Time can't be empty");
        }
        if (time.length() != (8 + idx)) {
            throw new IllegalArgumentException("Time must as format HH:mm:ss, " + time);
        }
        for (int i = idx; i < idx + 8; i++) {
            char c = time.charAt(i);
            if (i == (idx + 2) || i == (idx + 5)) {
                if (c != ':') {
                    throw new IllegalArgumentException("Time must as format HH:mm:ss, " + time);
                }

            } else if (c < '0' || c > '9')
                throw new IllegalArgumentException("Time must as format HH:mm:ss, " + time);
        }
        int hour = Integer.parseInt(time.substring(idx + 0, idx + 2));
        int minute = Integer.parseInt(time.substring(idx + 3, idx + 5));
        int second = Integer.parseInt(time.substring(idx + 6, idx + 8));
        if (hour < 0 || hour >= 24 || minute < 0 || minute > 59 || second < 0 || second > 59) {
            throw new IllegalArgumentException("Invalid time, " + time);
        }

        return new int[] { hour, minute, second };

    }

    public String toString(TimeZone zone, DateFormat format) {
        // zone = Calendars.getGMTTimeZone(zone);
        // for literal
        zone = Calendars.getGMTTimeZone(BASE_TIMEZONE);
        Date dt = new Date(utcOffset);
        format.setTimeZone(zone);
        return format.format(dt);
    }

    /**
     * 如果字符串的时间能转换成有效时间串
     *
     * @param time
     * @return
     */
    public static String getValidUFTimeString(String time) {
        if (time == null)
            return null;
        try {
            int[] t = internalParse(time);
            StringBuffer sb = new StringBuffer();
            append(sb, t[0], ':');
            append(sb, t[1], ':');
            if (t[2] < 10) {
                sb.append('0');
            }
            sb.append(t[2]);
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 如果字符串11-18位能转换成时间返回true。
     *
     * @param time
     * @return
     */
    public static boolean isAllowTime(String time) {
        try {
            internalParse(time);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String toPersisted() {
        return toStdString();
    }

}