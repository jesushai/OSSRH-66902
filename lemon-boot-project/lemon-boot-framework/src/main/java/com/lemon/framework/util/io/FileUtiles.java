package com.lemon.framework.util.io;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * @author zhanghai
 * create on 2018/11/8
 */
@Slf4j
@SuppressWarnings("unused")
public class FileUtiles {

    /**
     * 从URL地址中读取文件流并保存
     *
     * @param file    保存文件路径
     * @param address URL地址
     * @return 是否成功
     */
    public static boolean nativeDownloadFile(File file, String address) {
        try {
            URL url = new URL(address);

            //将url链接的文件以字节流的形式存储到 DataInputStream类中
            try (DataInputStream dis = new DataInputStream(url.openStream())) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    //定义字节数组大小
                    byte[] buffer = new byte[1024];

                    //从所包含的输入流中读取[buffer.length()]的字节，并将它们存储到缓冲区数组buffer 中。
                    //dataInputStream.read()会返回写入到buffer的实际长度,若已经读完 则返回-1
                    int length;
                    while ((length = dis.read(buffer)) > 0) {
                        //将buffer中的字节写入文件中区
                        fos.write(buffer, 0, length);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 清除文件夹下的所有文件
     *
     * @param path       文件夹
     * @param deleteRoot 是否删除根文件夹
     * @return 是否成功
     */
    public static boolean deleteAll(String path, boolean deleteRoot) {
        File parent = new File(path);
        return deleteAll(parent, deleteRoot);
    }

    /**
     * 清除文件夹下的所有文件
     *
     * @param parent     文件或文件夹
     * @param deleteRoot 是否删除根文件夹
     * @return 是否成功
     */
    public static boolean deleteAll(File parent, boolean deleteRoot) {

        if (parent == null || !parent.exists()) {
            return false;
        }

        // 是文件直接删除
        if (parent.isFile()) {
            return parent.delete();
        }

        File[] files = parent.listFiles();
        if (files == null) {
            return true;
        }

        for (File file : files) {
            if (!deleteAll(file, true)) {
                return false;
            }
        }

        if (deleteRoot) {
            return parent.delete();
        }
        return true;
    }
}
