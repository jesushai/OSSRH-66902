package com.lemon.framework.util.io;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public class InputStreamUtils {

    final static int BUFFER_SIZE = 4096;

    /**
     * 将InputStream转换成String，默认字符集ISO-8859-1
     *
     * @param in 输入流
     * @return 串
     * @throws IOException IOException
     */
    public static String InputStreamToString(InputStream in) throws IOException {
        return InputStreamToString(in, "ISO-8859-1");
    }

    /**
     * 将InputStream转换成某种字符编码的String
     *
     * @param in       输入流
     * @param encoding 字符集
     * @return 串
     * @throws IOException IOException
     */
    public static String InputStreamToString(InputStream in, String encoding) throws IOException {
        return new String(InputStreamToByte(in), encoding);
    }

    // 将InputStream转换成byte数组
    public static byte[] InputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);
        return outStream.toByteArray();
    }

    /**
     * 将String转换成InputStream，默认字符集ISO-8859-1
     *
     * @param in 输入串
     * @return 转换的输入流
     * @throws IOException IOException
     */
    public static InputStream StringToInputStream(String in) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes());
        IOUtils.toInputStream(in, "ISO-8859-1");
        return is;
    }

    /**
     * 将byte数组转换成InputStream
     *
     * @param in 输入的字节
     * @return 转换的输入流
     */
    public static InputStream byteToInputStream(byte[] in) {
        return new ByteArrayInputStream(in);
    }

    /**
     * 将byte数组转换成String
     *
     * @param in 字节
     * @return 串
     * @throws Exception Exception
     */
    public static String byteToString(byte[] in) throws Exception {
        InputStream is = byteToInputStream(in);
        return InputStreamToString(is);
    }

}
