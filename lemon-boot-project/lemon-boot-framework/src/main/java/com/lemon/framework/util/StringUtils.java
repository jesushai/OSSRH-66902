package com.lemon.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class StringUtils {
//
//    /**
//     * 从buffer中删除子串
//     *
//     * @param stringBuffer buffer
//     * @param subString    子串
//     * @param delete       是否直接删除
//     * @return true找到，false没有找到
//     */
//    public static boolean include(StringBuffer stringBuffer, String subString, boolean delete) {
//        boolean result = false;
//        if (stringBuffer != null && subString != null) {
//            int pos = stringBuffer.indexOf(subString);
//            if (pos >= 0) {
//                if (delete)
//                    stringBuffer.delete(pos, pos + subString.length());
//                result = true;
//            }
//        }
//        return result;
//    }

    public static String toLikeString(String value) {
        if (!value.startsWith("%"))
            value = "%" + value;
        if (!value.endsWith("%"))
            value = value + "%";
        return value;
    }

    /**
     * 统计指定内容在字符串中出现的次数
     *
     * @param src     字符串
     * @param keyword 查找的关键字
     * @return 出现的次数
     */
    public static int findStrCount(String src, String keyword) {
        int count = 0;
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(src);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public static String replace(String src, int start, int len, String newstr) {
        return src.substring(0, start) + newstr + src.substring(start + len);
    }

    public static String trim(Object src) {
        return src == null ? null : src.toString().trim();
    }

    /**
     * @param src 对象
     * @return 如果是null则返回空串，否则返回trim后的值
     */
    public static String nvl(Object src) {
        return src == null ? "" : src.toString().trim();
    }

    public static boolean isEmpty(Object src) {
        return (src == null || src.toString().equals(""));
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str 字符串
     * @return 是否是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static String trimWhitespace(String src) {
        if (src == null) {
            return null;
        } else {
            StringBuilder var1 = new StringBuilder();

            for (int var2 = 0; var2 < src.length(); ++var2) {
                char var3 = src.charAt(var2);
                if (var3 != '\n' && var3 != '\f' && var3 != '\r' && var3 != '\t') {
                    var1.append(var3);
                }
            }

            return var1.toString().trim();
        }
    }

    public static String getBytesPrintString(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder sb = new StringBuilder().append("{");
        for (byte b : bytes) {
            sb.append(b).append(",");
        }
        if (bytes.length > 0)
            sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    public static String fillLeft(Object str, char ch, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(ch);
        }
        sb.append(str);
        return sb.substring(sb.length() - len, sb.length());
    }

    /**
     * 将一个串自增1，范围是0-9/A-Z/a-z，长度固定
     *
     * @param str 字符串
     * @return 增加后的值
     * @throws Exception 到达上限，即zzz
     */
    public static String incrStr09AZaz(String str) throws Exception {
        if (str == null)
            return null;
        if (str.isEmpty())
            return str;
        char c = str.charAt(str.length() - 1);
        c++;
        if (c == ':') c = 'A';
        if (c == '[') c = 'a';
        if (c == '{') {
            // 上一级增加
            if (str.length() <= 1)
                throw new Exception("字符串已到上限无法增加。");
            else
                str = incrStr09AZaz(str.substring(0, str.length() - 1)) + '0';
        } else {
            str = str.substring(0, str.length() - 1) + c;
        }
        return str;
    }

    /**
     * 将一个串自增1，范围是0-9，长度固定
     *
     * @param str 字符串
     * @return 增加后的值
     * @throws Exception 到达上限，即zzz
     */
    public static String incrStrNumber(String str) throws Exception {
        if (str == null)
            return null;
        if (str.isEmpty())
            return str;
        char c = str.charAt(str.length() - 1);
        if (c < '0' || c > '9')
            throw new Exception("非数组无法自增或已到上限。");
        if (c == '9') {
            // 上一级增加
            if (str.length() <= 1)
                throw new Exception("字符串已到上限无法增加。");
            else
                str = incrStrNumber(str.substring(0, str.length() - 1)) + '0';
        } else {
            str = str.substring(0, str.length() - 1) + (++c);
        }
        return str;
    }

    public static String dateFormat(Date date, String patten) {
        if (StringUtils.isEmpty(patten))
            patten = "yyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        return simpleDateFormat.format(date);
    }

}
