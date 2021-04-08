package com.lemon.framework.util.crypto.password;

/**
 * 名称：密码工具
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@SuppressWarnings("unused")
public class PasswordEncoderUtil {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 密码加密，返回密文
     *
     * @param rawPassword 明文
     * @return 密文
     */
    public static String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 校验旧密码是否正确
     *
     * @param rawPassword     用户输入的明文
     * @param encodedPassword 数据库存储的密文
     * @return 是否正确
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
