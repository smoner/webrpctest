package com.smoner.rpc.demo2.framework.pub.lang;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * 工具类
 *
 *
 */
public class Calendars {

    private static TimeZone defZone;

    /**
     * 默认时区对应的标准GMT时区
     *
     * @return
     */
    public static TimeZone getGMTDefault() {
        if (defZone == null) {
            TimeZone zone = getGMTTimeZone(TimeZone.getDefault());
            TimeZone.setDefault(zone);
            return zone;
        }
        return defZone;
    }

    public static void setGMTDefault(TimeZone zone) {
        defZone = getGMTTimeZone(zone);
    }

    /**
     * 输入时区对应的标准GMT时区
     *
     * @param zone
     *            输入时区
     * @return
     */
    public static TimeZone getGMTTimeZone(TimeZone zone) {
        if (zone == null) {
            return getGMTDefault();
        }
        if (zone.getID().startsWith("GMT") || zone.getID().startsWith("UTC")) {
            return zone;
        } else {
            long rawOffset = zone.getRawOffset();
            long absrawOffset = rawOffset > 0 ? rawOffset : -rawOffset;
            int hourOffset = (int) absrawOffset / ICalendar.MILLIS_PER_HOUR;
            int minueOffset = (int) ((absrawOffset % ICalendar.MILLIS_PER_HOUR) / ICalendar.MILLIS_PER_MINUTE);
            return toGMTZone(rawOffset >= 0, hourOffset, minueOffset);
        }
    }

    /**
     * 输入时区ID对应的标准GMT时区
     *
     * @param id
     *            输入时区ID
     * @return
     */
    public static TimeZone getGMTTimeZone(String id) {
        if(id == null)
            return getGMTDefault();
        if (id.startsWith("UTC")) {
            id = id.replace("UTC", "GMT");
        }
        TimeZone zone = TimeZone.getTimeZone(id);
        return getGMTTimeZone(zone);
    }

    /**
     * 用指定格式的日期时间字符串构造UFDateTime
     *
     * @param date
     *            日期时间字符串
     * @param format
     *            输入的日期时间格式
     * @return
     */
    public static UFDateTime getUFDateTime(String date, DateFormat format) {
        try {
            Date d = format.parse(date);
            return new UFDateTime(d);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal date time: " + date);
        }
    }

    /**
     * 用指定格式的日期字符串构造UFDate
     *
     * @param date
     *            日期字符串
     * @param format
     *            输入的日期格式
     * @return
     */
    public static UFDate getUFDate(String date, DateFormat format) {
        try {
            Date d = format.parse(date);
            return new UFDate(d);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal date time: " + date);
        }
    }

    /**
     * UFDate在不同时区转换
     *
     * @param d
     *            日期字符串yyyy-MM-dd格式
     * @param tzFrom
     *            源时区ID
     * @param tzTo
     *            目的时区ID
     * @return 新时区的日期字符串yyyy-MM-dd格式
     */
    public static String convertDate(String d, String tzFrom, String tzTo) {
        UFDate date = new UFDate(d, getGMTTimeZone(tzFrom));
        return date.toStdString(getGMTTimeZone(tzTo));
    }

    /**
     * UFDateTime在不同时区转换
     *
     * @param dt
     *            日期时间字符串yyyy-MM-dd HH:mm:ss格式
     * @param tzFrom
     *            源时区ID
     * @param tzTo
     *            目的时区ID
     * @return 新时区的日期时间字符串yyyy-MM-dd HH:mm:ss格式
     */
    public static String convertDateTime(String dt, String tzFrom, String tzTo) {
        UFDateTime dateTime = new UFDateTime(dt, getGMTTimeZone(tzFrom));
        return dateTime.toStdString(getGMTTimeZone(tzTo));
    }

    private static TimeZone toGMTZone(boolean positive, int hourOffset,
                                      int minueOffset) {
        StringBuffer sb = new StringBuffer("GMT");
        sb.append(positive ? '+' : '-');
        sb = hourOffset > 9 ? sb.append(hourOffset) : sb.append('0').append(
                hourOffset);
        sb.append(':');
        sb = minueOffset > 9 ? sb.append(minueOffset) : sb.append('0').append(
                minueOffset);
        return TimeZone.getTimeZone(sb.toString());
    }

    /**
     * 将有效日期串字符串转换成转换成标准格式。
     *
     * @return 可以转换的日期字符串
     * @param 标准格式
     */
    public static String getValidUFDateString(String str) {
        int[] v = UFDate.internalParse(str);
        StringBuffer sb = new StringBuffer();
        append(sb, v[0], '-');
        append(sb, v[1], '-');
        if (v[2] < 10) {
            sb.append('0');
        }
        sb.append(v[2]);
        return sb.toString();
    }

    /**
     * 将有效日期时间串字符串转换成转换成标准格式。
     *
     * @return 可以转换的日期时间字符串
     * @param 标准格式
     */
    public static String getValidUFDateTimeString(String str) {
        int[] v = UFDateTime.internalParse(str);
        StringBuffer sb = new StringBuffer();
        append(sb, v[0], '-');
        append(sb, v[1], '-');
        append(sb, v[2], ' ');
        append(sb, v[3], ':');
        append(sb, v[4], ':');
        if (v[5] < 10) {
            sb.append('0');
        }
        sb.append(v[5]);

        return sb.toString();
    }

    /**
     * 将有效时间串字符串转换成转换成标准格式。
     *
     * @param time
     *            可以转换的时间字符串
     * @return 标准格式 时间字符串
     */
    public static String getValidUFTimeString(String time) {
        int[] v = UFTime.internalParse(time);
        StringBuffer sb = new StringBuffer();
        append(sb, v[0], ':');
        append(sb, v[1], ':');
        if (v[2] < 10) {
            sb.append('0');
        }
        sb.append(v[2]);
        return sb.toString();
    }

    private static void append(StringBuffer sb, int v, char split) {
        if (v < 10) {
            sb.append('0');
        }
        sb.append(v).append(split);
    }

    /**
     * 根据日期、时间获得日期时间格林威治时间
     *
     * @param date
     * @param time
     * @param zone
     * @return
     */
    public static long getMillis(UFDate date, UFTime time, TimeZone zone) {
        zone = Calendars.getGMTTimeZone(zone);
        long mills = date.getMillis() + time.getMillis() + zone.getRawOffset();
        if (mills < date.getMillis()) {
            mills += ICalendar.MILLIS_PER_DAY;
        } else if (mills >= (date.getMillis() + ICalendar.MILLIS_PER_DAY)) {
            mills -= ICalendar.MILLIS_PER_DAY;
        }
        return mills;
    }
}