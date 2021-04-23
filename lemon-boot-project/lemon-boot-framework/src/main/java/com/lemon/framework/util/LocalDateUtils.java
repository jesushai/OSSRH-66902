package com.lemon.framework.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@SuppressWarnings("unused")
public class LocalDateUtils {
    //默认使用系统当前时区
    private static final ZoneId ZONE = ZoneId.systemDefault();

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    public static final String TIME_NOFUII_FORMAT = "yyyyMMddHHmmss";

    private static final String REGEX = "\\:|\\-|\\s";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据传入的时间格式返回系统当前的时间
     *
     * @param format string
     * @return 字符串表达
     */
    public static String timeByFormat(String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime now = LocalDateTime.now();
        return now.format(dateTimeFormatter);
    }

    /**
     * 将Date转换成LocalDateTime
     *
     * @param d date
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date d) {
        Instant instant = d.toInstant();
        return LocalDateTime.ofInstant(instant, ZONE);
    }

    /**
     * 将Date转换成LocalDate
     *
     * @param d date
     * @return LocalDateTime
     */
    public static LocalDate dateToLocalDate(Date d) {
        Instant instant = d.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE);
        return localDateTime.toLocalDate();
    }

    /**
     * 将Date转换成LocalTime
     *
     * @param d date
     * @return LocalDateTime
     */
    public static LocalTime dateToLocalTime(Date d) {
        Instant instant = d.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE);
        return localDateTime.toLocalTime();
    }

    /**
     * 将LocalDate转换成Date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date localDateToDate(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay().atZone(ZONE).toInstant();
        return Date.from(instant);
    }

    /**
     * 将LocalDateTime转换成Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZONE).toInstant();
        return Date.from(instant);
    }

    /**
     * 将相应格式yyyy-MM-dd yyyy-MM-dd HH:mm:ss 时间字符串转换成Date
     *
     * @param time   string
     * @param format string
     * @return date
     */
    public static Date stringToDate(String time, String format) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(format);
        if (DATE_FORMAT_DEFAULT.equals(format)) {
            return LocalDateUtils.localDateTimeToDate(LocalDateTime.parse(time, f));
        } else if (DATE_FORMAT.equals(format)) {
            return LocalDateUtils.localDateToDate(LocalDate.parse(time, f));
        }
        return null;
    }

    /**
     * time 类型等于Date返回String<p>
     * time 类型等于String返回对应格式化日期类型<p>
     * time 等于String 暂时支持format为yyyy-MM-dd. yyyy-MM-dd HH:mm:ss. yyyyMMddHHmmss<p>
     * time 等于Date 不限制格式化类型，返回String<p>
     *
     * @param time   string or date
     * @param format string
     * @param <T>    T
     * @return localDateTime or localDate or string
     */
    @SuppressWarnings("unchecked")
    public static <T> T timeFormat(T time, String format) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(format);
        //暂时支持三种格式转换
        if (ClassIdentical.isCompatible(String.class, time)) {
            if (DATE_FORMAT_DEFAULT.equals(format)) {
                LocalDateTime localDateTime = LocalDateTime.parse(time.toString(), f);
                return (T) localDateTime.atZone(ZONE).toInstant();
            }
            if (DATE_FORMAT.equals(format)) {
                LocalDate localDate = LocalDate.parse(time.toString(), f);
                return (T) localDate;
            }
            if (TIME_NOFUII_FORMAT.equals(format)) {
                String rp = time.toString().replaceAll(REGEX, "");
                LocalDateTime localDate = LocalDateTime.parse(time.toString(), f);
                return (T) localDate;
            }
        }
        if (ClassIdentical.isCompatible(Date.class, time)) {
            Date date = (Date) time;
            Instant instant = date.toInstant();
            instant.atZone(ZONE).format(f);
            return (T) instant.atZone(ZONE).format(f);
        }
        return null;
    }

    /**
     * 根据ChronoUnit枚举计算两个时间差，日期类型对应枚举
     * 注:注意时间格式，避免cu选择不当的类型出现的异常
     *
     * @param cu chronoUnit.enum.key
     * @param d1 date
     * @param d2 date
     * @return 时间差
     */
    public static long chronoUnitBetweenByDate(ChronoUnit cu, Date d1, Date d2) {
        return cu.between(LocalDateUtils.dateToLocalDateTime(d1), LocalDateUtils.dateToLocalDateTime(d2));
    }

    /**
     * 根据ChronoUnit枚举计算两个时间差，日期类型对应枚举
     * 注:注意时间格式，避免cu选择不当的类型出现的异常
     *
     * @param cu         chronoUnit.enum.key
     * @param s1         string
     * @param s2         string
     * @param dateFormat 格式
     * @return 时间差
     */
    public static Long chronoUnitBetweenByString(ChronoUnit cu, String s1, String s2, String dateFormat) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(dateFormat);
        if (DATE_FORMAT_DEFAULT.equals(dateFormat)) {
            LocalDateTime l1 = LocalDateUtils.dateToLocalDateTime(LocalDateUtils.stringToDate(s1, dateFormat));
            LocalDateTime l2 = LocalDateUtils.dateToLocalDateTime(LocalDateUtils.stringToDate(s2, dateFormat));
            return cu.between(l1, l2);
        }
        if (DATE_FORMAT.equals(dateFormat)) {
            LocalDate l1 = LocalDateUtils.dateToLocalDate(LocalDateUtils.stringToDate(s1, dateFormat));
            LocalDate l2 = LocalDateUtils.dateToLocalDate(LocalDateUtils.stringToDate(s2, dateFormat));
            return cu.between(l1, l2);
        }
        if (TIME_NOFUII_FORMAT.equals(dateFormat)) {
            LocalDateTime l1 = LocalDateTime.parse(s1.replaceAll(REGEX, ""), f);
            LocalDateTime l2 = LocalDateTime.parse(s2.replaceAll(REGEX, ""), f);
            return cu.between(l1, l2);
        }
        return null;
    }

    /**
     * 根据ChronoUnit枚举计算两个时间相加，日期类型对应枚举
     * 注:注意时间格式，避免cu选择不当的类型出现的异常
     *
     * @param cu chronoUnit.enum.key
     * @param d1 date
     * @param d2 long
     * @return 偏移后的时间
     */
    public static Date chronoUnitPlusByDate(ChronoUnit cu, Date d1, long d2) {
        return LocalDateUtils.localDateTimeToDate(cu.addTo(LocalDateUtils.dateToLocalDateTime(d1), d2));
    }

    /**
     * 将time时间转换成毫秒时间戳
     *
     * @param time string
     * @return long
     */
    public static long stringDateToMilli(String time) {
        return LocalDateUtils.stringToDate(time, DATE_FORMAT_DEFAULT).toInstant().toEpochMilli();
    }

    /**
     * 将毫秒时间戳转换成Date
     *
     * @param time string
     * @return date
     */
    public static Date timeMilliToDate(String time) {
        return Date.from(Instant.ofEpochMilli(Long.parseLong(time)));
    }

}
