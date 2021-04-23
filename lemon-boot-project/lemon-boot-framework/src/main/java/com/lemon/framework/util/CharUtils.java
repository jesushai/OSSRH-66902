package com.lemon.framework.util;

import java.util.Random;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@SuppressWarnings("unused")
public class CharUtils {

    public static String getRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        return getRandomString(num, base);
    }

    public static String getRandomNum(Integer num) {
        String base = "0123456789";
        return getRandomString(num, base);
    }

    private static String getRandomString(Integer num, String base) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
