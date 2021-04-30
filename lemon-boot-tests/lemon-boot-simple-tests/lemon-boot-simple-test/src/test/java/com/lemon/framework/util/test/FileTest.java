package com.lemon.framework.util.test;

import com.lemon.framework.util.io.FileUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/28
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileTest {

    @Test
    @Order(1)
    void testFileUtilInsertLine() throws IOException {
        // 加三行
        FileUtil.insertLines("c.txt", 1, "1111", "2222", "3333");
    }

    @Test
    @Order(2)
    void testFileUtilRemoveLine() throws IOException {
        // 删第一行1111
        FileUtil.removeLines("c.txt", 1, -1);
    }

    @Test
    @Order(3)
    void testFileUtilUpdateLine() throws IOException {
        // 更改最后一行3333，变成两行
        FileUtil.updateLines("c.txt", 2, 2, "foo", "bar");
    }

    @Test
    @Order(4)
    void testCorrectFileLineSeparatorBySystem() throws IOException {
        // 尝试修正文件
        FileUtil.correctLineSeparatorBySystem("c.txt");
    }

    @Test
    @Order(5)
    void testSystemSeparator() {
        System.out.println(":" + Arrays.toString(System.lineSeparator().getBytes()) + ":");
        String sep = System.lineSeparator();
        if ("\r\n".equals(sep)) {
            System.out.println("Windows");
        } else if ("\n".equals(sep)) {
            System.out.println("Mac");
        } else if ("\r".equals(sep)) {
            System.out.println("Linux/Unix");
        } else {
            System.out.println("Unknown");
        }
    }

    @Test
    @Order(6)
    void testReadLine() throws IOException {
        System.out.println("===");
        String[] arr = new String[] {
            "2222", "foo", "bar"
        };
        try (RandomAccessFile r = new RandomAccessFile("c.txt", "rw")) {
            for (int i = 0; i < 3; i++) {
                String s = r.readLine();
                System.out.println(s + ":pointer=" + r.getFilePointer());
                if (null == s)
                    break;
                Assertions.assertEquals(s, arr[i]);
            }
        }
        System.out.println("===");
        FileUtils.deleteQuietly(new File("c.txt"));
    }
}
