package com.lemon.framework.util;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("unused")
public class Convert {

    public static String toString(String str) {
        if (str == null) {
            return "";
        }
        return "'" + str + "'";
    }

    public static double toDouble(String str) {
        if ((str == null) || (str.equals(""))) {
            return 0.0D;
        }
        return Double.parseDouble(str);
    }

    public static String toType(String str, String type) {
        if ((type.equalsIgnoreCase("字符")) || (type.equalsIgnoreCase("日期"))) {
            if (str == null) {
                return "";
            }
            str = "'" + str + "'";
            return str;
        }

        if (str == null) {
            return "0";
        }
        return str;
    }

    public static String toNotNull(String str) {
        if (str == null)
            str = "";
        return str;
    }

    public static String insert(String string, String ins, String str) {
        if ((string == null) || (ins == null) || (str == null))
            return null;
        String newStr = string;
        int i = newStr.indexOf(str);
        while (i != -1) {
            newStr = newStr.substring(0, i) + ins + newStr.substring(i);
            i += ins.length() + str.length();
            i = newStr.indexOf(str, i);
        }
        return newStr;
    }

    public static String replace(String string, String oldstr, String newstr) {
        if ((string == null) || (oldstr == null) || (newstr == null))
            return null;
        String newStr = string;
        int i = newStr.indexOf(oldstr);
        while (i != -1) {
            newStr = newStr.substring(0, i) + newstr + newStr.substring(i + oldstr.length());
            i += newstr.length();
            i = newStr.indexOf(oldstr, i);
        }
        return newStr;
    }

    public static String append(String string, String str, String app) {
        if ((string == null) || (str == null) || (app == null))
            return null;
        String newStr = string;
        int i = newStr.indexOf(str);
        while (i != -1) {
            newStr = newStr.substring(0, i + str.length()) + app + newStr.substring(i + str.length());
            i += str.length() + app.length();
            i = newStr.indexOf(str, i);
        }
        return newStr;
    }

    public static String ltrim(String str) {
        if (str == null) {
            return null;
        }
        for (int i = 0; i < str.length(); ++i)
            if (str.charAt(i) != ' ')
                return str.substring(i);
        return str;
    }

    public static String rtrim(String str) {
        if (str == null) {
            return null;
        }
        for (int i = str.length() - 1; i >= 0; --i)
            if (str.charAt(i) != ' ')
                return str.substring(0, i + 1);
        return str;
    }

    public static String ShowMath(double d, int len, boolean flag) {
        return ShowMath(Double.toString(d), len, flag);
    }

    public static String ShowMath(String str, int len, boolean flag) {
        try {
            BigDecimal tmp = new BigDecimal(str);
            return tmp.setScale(len, 4).toString();
        } catch (Exception ignore) {
        }
        return str;
    }

    public static String RoundMath(String str) {
        try {
            BigDecimal tmp = new BigDecimal(str);
            return tmp.setScale(0, 0).toString();
        } catch (Exception ignore) {
        }
        return str;
    }

    public static String NumToChinese(String input) {
        String s1 = "零壹贰叁肆伍陆柒捌玖";
        String s4 = "分角整元拾佰仟万拾佰仟亿拾佰仟";
        String temp;
        String result = "";
        String val = "0123456789.";
        if (input == null) {
            return "0";
        }
        String zf = "";
        if ((input.length() > 0) && (input.substring(0, 1).equals("-"))) {
            zf = "负";
            input = input.substring(1);
        }
        temp = ShowMath(input, 2, false);
        for (int i = 0; i < temp.length(); ++i) {
            String str = temp.substring(i, i + 1);
            if (!val.contains(str)) {
                return "0";
            }
        }
        int len;
        if (!temp.contains("."))
            len = temp.length();
        else {
            len = temp.indexOf(".");
        }
        if (len > s4.length() - 3) {
            return "0";
        }
        int n1;
        String num;
        String unit;

        int is_yi = 11;
        int is_wan = 7;
        int is_yuan = 3;

        for (int i = 0; i < temp.length(); ++i) {
            if (i > len + 2) {
                break;
            }
            if (i == len) {
                continue;
            }
            n1 = Integer.parseInt(String.valueOf(temp.charAt(i)));
            num = s1.substring(n1, n1 + 1);
            n1 = len - i + 2;
            unit = s4.substring(n1, n1 + 1);

            if ((n1 == is_yi) && (num.equals("零"))) {
                if (result.lastIndexOf("零") == result.length() - 1)
                    result = result.substring(0, result.lastIndexOf("零"));
                result = result.concat("亿");
            }

            if ((n1 == is_wan) && (num.equals("零"))) {
                if (result.lastIndexOf("零") == result.length() - 1)
                    result = result.substring(0, result.lastIndexOf("零"));
                result = result.concat("万");
            }

            if ((n1 == is_yuan) && (num.equals("零")) && (result.length() > 0)) {
                if (result.lastIndexOf("零") == result.length() - 1) {
                    result = result.substring(0, result.lastIndexOf("零"));
                }
                result = result.concat("元");
            }

            if ((num.equals("零")) && (result.lastIndexOf("零") != result.length() - 1))
                result = result.concat(num);
            if (!(num.equals("零"))) {
                result = result.concat(num).concat(unit);
            }
        }
        if ((result.length() > 0) && (result.lastIndexOf("零") == result.length() - 1)) {
            result = result.substring(0, result.lastIndexOf("零"));
        }
        if ((len == temp.length()) || (len == temp.length() - 1)) {
            result = result.concat("整");
        }
        return zf + result;
    }

    public static String cleanCipher(String str) {
        try {
            if (str == null) {
                return "";
            }
            if (Double.parseDouble(str) == 0.0D) {
                return "";
            }
            return str;
        } catch (Exception ignore) {
        }
        return str;
    }

    public static String removeEndStr(String str, String divstr) {
        if ((str.length() > divstr.length()) && (str.lastIndexOf(divstr) == str.length() - divstr.length()))
            str = str.substring(0, str.length() - divstr.length());
        return str;
    }

    public static String toISO(String str) {
        if (str == null)
            return null;
        try {
            return new String(str.getBytes("GB2312"), "8859_1");
        } catch (Exception ignore) {
        }
        return null;
    }

    public static String toGBK(String str) {
        if (str == null)
            return null;
        try {
            return URLEncoder.encode(str, "gb2312");
        } catch (Exception ignore) {
        }
        return null;
    }

    public static String toHTML(String string) {
        String newStr = string;

        newStr = newStr.replace("&", "&amp;");
        newStr = newStr.replace("\"", "&quot;");
        newStr = newStr.replace("<", "&lt;");
        newStr = newStr.replace(">", "&gt;");

        newStr = newStr.replace("\r\n", "<br>");

        return newStr;
    }

    public static String byte2hex(byte... b) {
        StringBuilder rst = new StringBuilder();
        String tmp;
        for (byte value : b) {
            tmp = Integer.toHexString(value & 0xff);

            if (tmp.length() == 1)
                rst.append("0").append(tmp);
            else
                rst.append(tmp);
        }
        return rst.toString().toUpperCase();
    }

    public static byte[] hex2byte(String S) {
        byte[] b = new byte[S.length() / 2];
        for (int n = 0; n < S.length() / 2; n++) {
            b[n] = (byte) (Integer.parseInt(S.substring(n * 2, n * 2 + 2), 16));
        }
        return b;
    }

    public static String formatAmount(Double d, int digits) {
        StringBuilder format = new StringBuilder("#,###");
        if (digits > 0)
            format.append(".");
        for (int i = 0; i < digits; i++) {
            if (i > 3)
                break;
            format.append("#");
        }
        NumberFormat nf = new DecimalFormat(format.toString());
        return nf.format(d);
    }

    public static void main(String[] args) {
        System.out.println(NumToChinese("-4431013"));

        String str = "43215.50";
        str = RoundMath(str);
        int tmp = Integer.parseInt(str);
        if (tmp % 10 > 0) {
            str = Integer.toString(tmp / 10 * 10 + 10);
        }
        System.out.println(str);
        System.out.println(Convert.formatAmount(123456.25135d, 4));
    }
}
