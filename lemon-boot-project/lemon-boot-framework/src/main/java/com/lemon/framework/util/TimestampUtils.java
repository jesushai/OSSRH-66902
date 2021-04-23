package com.lemon.framework.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class TimestampUtils {
    public static final String SYBASE_TIMESTAMP_FIRST = "0x0000000000000000";
    public static final String ORACLE_TIMESTAMP_FIRST = "1980/01/01 00:00:00:000";
    public static final String JL_TIMESTAMP_FIRST = "19800101000000";

    public static String parse(Timestamp t) {
        return parse(t, '/');
    }

    /**
     * 格式化时间戳
     *
     * @param t         时间戳
     * @param separator 日期分隔符：/或-，默认-
     * @return 返回字符串表达的时间戳 yyyy-MM-dd hh:mm:ss:SSS
     */
    public static String parse(Timestamp t, char separator) {
        if (t == null)
            return null;
        SimpleDateFormat sf;
        if (separator == '/')
            sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        else
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return sf.format(t);
    }

    public static String parseToDate(Timestamp t) {
        return parseToDate(t, '/');
    }

    public static String parseToDate(Timestamp t, char separator) {
        if (t == null)
            return null;
        SimpleDateFormat sf;
        if (separator == '/')
            sf = new SimpleDateFormat("yyyy/MM/dd");
        else
            sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(t);
    }

    /**
     * 将日期或时间文本转换为时间戳类型
     *
     * @param s 时间或日期文本，支持格式：yyyy-MM-dd | yyyy-MM-dd hh:mm:ss | yyyy-MM-dd hh:mm:ss.ffffff<p>
     *          分隔符可以是-也可以是/，可以带毫秒也可以不带，毫秒分隔符可以是.或:，毫秒可以是3位或6位
     * @return Timestamp
     */
    public static Timestamp parse(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        if (s.lastIndexOf("/") > 0)
            s = s.replaceAll("/", "-");
        if (org.springframework.util.StringUtils.countOccurrencesOf(s, ":") > 2) {
            int pos = s.lastIndexOf(":");
            s = s.substring(0, pos) + "." + s.substring(pos + 1);
        }
        if (!s.contains(":")) {
            s = s + " 00:00:00";
        }
        try {
            return Timestamp.valueOf(s);
        } catch (Exception e) {
            throw new RuntimeException("日期时间格式错误！");
        }
    }

    public static Timestamp getOffsetDate(Date date, int offset, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, offset);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * 返回当前日期，不包含时间
     *
     * @return 当前日期
     */
    public static Timestamp today() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new Timestamp(c.getTimeInMillis());
    }

    public static Timestamp now() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static Timestamp date(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp datetime(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 改变时间戳的某部分的值
     *
     * @param t     时间戳
     * @param field 哪个部分 Calender.X
     * @param value 值
     * @return 新的时间戳
     */
    public static Timestamp set(Timestamp t, int field, int value) {
        Calendar c = Calendar.getInstance();
        c.setTime(t);
        c.set(field, value);
        return new Timestamp(c.getTimeInMillis());
    }

    public static int get(Timestamp t, int field) {
        Calendar c = Calendar.getInstance();
        c.setTime(t);
        return c.get(field);
    }

    private static Calendar _trimTime(Date t) {
        Calendar c = Calendar.getInstance();
        c.setTime(t);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static Timestamp trimTime(Date t) {
        if (t == null) {
            return null;
        }
        return new Timestamp(_trimTime(t).getTimeInMillis());
    }

    public static Date trimTimeToDate(Date t) {
        if (t == null) {
            return null;
        }
        return _trimTime(t).getTime();
    }

    public static Timestamp dateToTimestamp(Date t) {
        if (t instanceof Timestamp)
            return (Timestamp) t;
        return new Timestamp(t.getTime());
    }

}
