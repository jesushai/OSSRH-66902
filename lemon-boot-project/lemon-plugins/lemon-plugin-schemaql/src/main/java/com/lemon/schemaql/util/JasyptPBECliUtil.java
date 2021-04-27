package com.lemon.schemaql.util;

import org.jasypt.intf.service.JasyptStatelessService;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/24
 */
public class JasyptPBECliUtil {

    public static String decrypt(String input, String password, String algorithm, String ivGeneratorClassName) {
        return new JasyptStatelessService().decrypt(
                input,
                password,
                null,
                null,
                algorithm,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                ivGeneratorClassName,
                null,
                null
        );
    }

    public static String encrypt(String input, String password, String algorithm, String ivGeneratorClassName) {
        return new JasyptStatelessService().encrypt(
                input,
                password,
                null,
                null,
                algorithm,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                ivGeneratorClassName,
                null,
                null
        );
    }
}
