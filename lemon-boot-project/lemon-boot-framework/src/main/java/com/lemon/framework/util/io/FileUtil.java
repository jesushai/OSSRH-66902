package com.lemon.framework.util.io;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author zhanghai
 * create on 2018/11/8
 */
@Slf4j
public class FileUtil {

    /**
     * 将文件的换行符替换成当前系统的换行符
     * <p>
     * 因为文件最初创建的操作系统与当前运行的系统可能并非同一种
     * <p>
     * 例如Windows系统创建的文件换行符是\r\n，而Linux则应该改为\r
     * <p>
     * 为了避免在运行期的系统读取文件出现偏差，可以做此操作
     *
     * @param path 文件路径
     * @throws IOException IOException
     */
    public static void correctLineSeparatorBySystem(String path) throws IOException {

        if (StringUtils.isBlank(path))
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File path cannot be empty.")
                    .throwIt();

        // 判断换行符是否匹配
        String fileLineSeparator = null;
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            String line = file.readLine();
            long offset = file.getFilePointer();    // 第一行末尾的位置（含换行符如果有的话）
            if (null != line && offset > line.length()) {
                // 有换行符
                file.seek(line.length());
                // 读取偏差的字符，即换行符
                byte[] bytes = new byte[(int) (offset - line.length())];
                file.read(bytes, 0, (int) (offset - line.length()));
                fileLineSeparator = new String(bytes);
            }
        }

        // 如果匹配则不必再执行了
        if (null == fileLineSeparator || fileLineSeparator.equals(System.lineSeparator())) {
            return;
        }

        // 开始逐行转换
        LoggerUtils.info(log,
                "Try to converted the file line separator ({}) to system line separator ({}), File={}.",
                fileLineSeparator, System.lineSeparator(), path);
        // 通过临时文件，最后将临时文件改名即可
        try (RandomAccessFile file = new RandomAccessFile(path + "~", "rw");
             LineIterator iterator = FileUtils.lineIterator(new File(path))) {

            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                file.write((line + System.lineSeparator()).getBytes());
            }
        }

        // 删原文件
        FileUtils.deleteQuietly(new File(path));
        // 改名
        File tempFile = new File(path + "~");
        tempFile.renameTo(new File(path));
    }

    /**
     * 删除多行文本
     *
     * @param path      文件路径
     * @param beginLine 开始删除的行，从1开始，小于等于0或大于最大行无效
     * @param endLine   结束行，小于等于beginLine则无效，即只删除beginLine指定的那一行
     * @throws IOException IOException
     */
    public static void removeLines(String path, long beginLine, long endLine) throws IOException {

        // 判断输入
        if (beginLine <= 0)
            return;

        if (endLine < beginLine)
            endLine = beginLine;

        if (StringUtils.isBlank(path))
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File path cannot be empty.")
                    .throwIt();

        try (RandomAccessFile file = new RandomAccessFile(path, "rw");
             RandomAccessFile temp = new RandomAccessFile(path + '~', "rw")) {

            long fileSize = file.length();
            int currentLine = 1;

            for (; currentLine < beginLine; currentLine++) {
                // 跳过之前的行
                String readLine = file.readLine();

                if (null == readLine) {
                    // 到文件尾部了
                    currentLine--;
                    break;
                }
            }

            // beginLine大于文件最大行数，无需删除
            if (currentLine < beginLine) {
                return;
            }

            long offset = file.getFilePointer();
            // 执行删除
            try (FileChannel source = file.getChannel();
                 FileChannel target = temp.getChannel()) {

                // 传输后面的部分到临时文件里，并截取传输的部分
                source.transferTo(offset, (fileSize - offset), target);
                source.truncate(offset);

                // 移动到临时文件开头
                temp.seek(0L);
                // 删除多少行意味着忽略多少行
                while (currentLine <= endLine) {
                    String readLine = temp.readLine();
                    if (null == readLine) {
                        // 全都删就完了
                        break;
                    }
                    currentLine++;
                }

                // 后面全都删除就不用往回传输了
                if (temp.getFilePointer() == temp.length()) {
                    return;
                }

                // 将剔除掉之后的部分再传输回来
                target.position(temp.getFilePointer());
                source.transferFrom(target, offset, (fileSize - offset - temp.getFilePointer()));
            }
        } finally {
            // 最后尝试删除临时文件
            try {
                File file = new File(path + '~');
                if (file.exists())
                    FileUtils.deleteQuietly(file);
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 插入多行文本到指定的行后
     *
     * @param path     文件路径
     * @param addLines 要插入的文本，不必添加回车换行符，这里会自动添加
     * @param line     指定行之后插入行，如果line>=maxLine则追加到末尾，<=0则插入第一行
     * @throws IOException IOException
     */
    public static void insertLines(String path, long line, String... addLines) throws IOException {

        // 判断输入
        if (null == addLines || addLines.length <= 0)
            return;

        if (StringUtils.isBlank(path))
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File path cannot be empty.")
                    .throwIt();

        try (RandomAccessFile file = new RandomAccessFile(path, "rw");
             RandomAccessFile temp = new RandomAccessFile(path + '~', "rw")) {

            int lineSeparatorLength = System.lineSeparator().length();
            long fileSize = file.length();
            int currentLine = 0;

            for (; currentLine < line; currentLine++) {
                // 跳过之前的行
                String readLine = file.readLine();

                if (null == readLine) {
                    // 到文件尾部了
                    break;
                }
            }

            long offset = file.getFilePointer();
            boolean appendLineSeparator = false;
            // 判断最后是否有换行符
            if (offset >= lineSeparatorLength) {
                byte[] bytes = new byte[lineSeparatorLength];
                file.seek(offset - lineSeparatorLength);
                if (file.read(bytes, 0, lineSeparatorLength) > 0) {
                    appendLineSeparator = !Arrays.equals(bytes, System.lineSeparator().getBytes());
                } else {
                    appendLineSeparator = true;
                }
            }

            // 执行插入
            try (FileChannel source = file.getChannel();
                 FileChannel target = temp.getChannel()) {

                // 传输后面的部分到临时文件里，并截取传输的部分
                source.transferTo(offset, (fileSize - offset), target);
                source.truncate(offset);

                // 移动到文件末尾
                file.seek(offset);
                // 追加内容，就是要插入的部分
                // 首行要追加的需要判断前面是否需要增加换行符
                if (appendLineSeparator) {
                    addLines[0] = System.lineSeparator() + addLines[0];
                }
                for (String s : addLines) {
                    file.write((s + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                }

                // 将挪走的部分再传输回来
                long newOffset = file.getFilePointer();
                target.position(0L);
                source.transferFrom(target, newOffset, (fileSize - offset));
            }
        } finally {
            // 最后尝试删除临时文件
            try {
                File file = new File(path + '~');
                if (file.exists())
                    FileUtils.deleteQuietly(file);
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 将指定范围的行替换成新的文本行
     *
     * @param path      文件路径
     * @param beginLine 开始删除的行，从1开始，小于等于0或大于最大行无效
     * @param endLine   结束行，小于等于beginLine则无效，即只删除beginLine指定的那一行
     * @param newLines  要替换的文本行，不必添加回车换行符，这里会自动添加
     * @throws IOException IOException
     */
    public static void updateLines(String path, long beginLine, long endLine, String... newLines) throws IOException {

        // 判断输入
        if (null == newLines || newLines.length <= 0)
            return;

        if (beginLine <= 0)
            return;

        if (endLine < beginLine)
            endLine = beginLine;

        if (StringUtils.isBlank(path))
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File path cannot be empty.")
                    .throwIt();

        try (RandomAccessFile file = new RandomAccessFile(path, "rw");
             RandomAccessFile temp = new RandomAccessFile(path + '~', "rw")) {

            long fileSize = file.length();
            int currentLine = 1;

            for (; currentLine < beginLine; currentLine++) {
                // 跳过之前的行
                String readLine = file.readLine();

                if (null == readLine) {
                    // 到文件尾部了
                    currentLine--;
                    break;
                }
            }

            // beginLine大于文件最大行数，无需删除
            if (currentLine < beginLine) {
                return;
            }

            long offset = file.getFilePointer();
            boolean appendLineSeparator = false;
            int lineSeparatorLength = System.lineSeparator().length();
            // 判断最后是否有换行符
            if (offset >= lineSeparatorLength) {
                byte[] bytes = new byte[lineSeparatorLength];
                file.seek(offset - lineSeparatorLength);
                if (file.read(bytes, 0, lineSeparatorLength) > 0) {
                    appendLineSeparator = !Arrays.equals(bytes, System.lineSeparator().getBytes());
                } else {
                    appendLineSeparator = true;
                }
            }

            // 执行替换
            try (FileChannel source = file.getChannel();
                 FileChannel target = temp.getChannel()) {

                // 传输后面的部分到临时文件里，并截取传输的部分
                source.transferTo(offset, (fileSize - offset), target);
                source.truncate(offset);

                // 移动到文件末尾
                file.seek(offset);
                // 追加内容，就是要插入的部分
                // 首行要追加的需要判断前面是否需要增加换行符
                if (appendLineSeparator) {
                    newLines[0] = System.lineSeparator() + newLines[0];
                }
                for (String s : newLines) {
                    file.write((s + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                }
                // 添加后的新位置
                long newOffset = file.getFilePointer();

                // 移动到临时文件开头
                temp.seek(0L);
                // 删除多少行意味着忽略多少行
                while (currentLine <= endLine) {
                    String readLine = temp.readLine();
                    if (null == readLine) {
                        // 全都删就完了
                        break;
                    }
                    currentLine++;
                }

                // 后面全都删除就不用往回传输了
                if (temp.getFilePointer() == temp.length()) {
                    return;
                }

                // 将剔除掉之后的部分再传输回来
                target.position(temp.getFilePointer());
                source.transferFrom(target, newOffset, (fileSize - offset - temp.getFilePointer()));
            }
        } finally {
            // 最后尝试删除临时文件
            try {
                File file = new File(path + '~');
                if (file.exists())
                    FileUtils.deleteQuietly(file);
            } catch (Exception ignore) {
            }
        }
    }

    public static void insert(String path, byte[] data, long offset) throws IOException {

        // 判断输入
        if (null == data || data.length <= 0)
            return;

        if (StringUtils.isBlank(path))
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File path cannot be empty.")
                    .throwIt();

        try (RandomAccessFile file = new RandomAccessFile(path, "rw");
             RandomAccessFile temp = new RandomAccessFile(path + '~', "rw")) {

            long fileSize = file.length();

            // 执行插入
            try (FileChannel source = file.getChannel();
                 FileChannel target = temp.getChannel()) {

                // 传输后面的部分到临时文件里，并截取传输的部分
                source.transferTo(offset, (fileSize - offset), target);
                source.truncate(offset);

                // 移动到文件末尾
                file.seek(offset);
                // 追加内容，就是要插入的部分
                // 首行要追加的需要判断前面是否需要增加换行符
                file.write(data);

                // 将挪走的部分再传输回来
                long newOffset = file.getFilePointer();
                target.position(0L);
                source.transferFrom(target, newOffset, (fileSize - offset));
            }
        } finally {
            // 最后尝试删除临时文件
            try {
                File file = new File(path + '~');
                if (file.exists())
                    FileUtils.deleteQuietly(file);
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 合并路径
     *
     * @param path 按顺序合并
     * @return 合并后的路径
     */
    public static String mergePath(String... path) {
        if (null != path && path.length > 0) {
            StringBuilder sb = new StringBuilder(path[0]);

            for (int i = 1; i < path.length; i++) {
                int t = 0;
                if (null == path[i] || path[i].isEmpty()) {
                    break;
                }

                char endChar = sb.charAt(sb.length() - 1);
                if (endChar == '/' || endChar == '\\' || endChar == File.separatorChar) {
                    t++;
                }

                if (path[i].startsWith("/") || path[i].startsWith("\\") || path[i].startsWith(File.separator)) {
                    t++;
                }

                switch (t) {
                    case 0:
                        sb.append(File.separatorChar).append(path[i]);
                        break;
                    case 1:
                        sb.append(path[i]);
                        break;
                    case 2:
                        sb.append(path[i].substring(1));
                        break;
                }
            }
            return sb.toString();
        }
        return null;
    }

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
