package com.lemon.schemaql.engine.helper;

import com.google.common.base.Splitter;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.support.i18n.I18NConfig;
import com.lemon.schemaql.config.support.i18n.LocaleNode;
import com.lemon.schemaql.config.support.i18n.MessageItem;
import com.lemon.schemaql.config.support.i18n.MessageKind;
import com.lemon.schemaql.engine.parser.json.ProjectSchemaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.lemon.schemaql.engine.SchemaQlContext.projectSchemaConfig;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/28
 */
public class I18NHelper {

    public static boolean enabled() {
        return CollectionUtils.isNotEmpty(projectSchemaConfig().getI18n());
    }

    /**
     * 获取国际化资源的基础Bundle名
     *
     * @return Bundle名
     */
    public static String[] getAllResourceBaseName() {
        if (enabled()) {
            return (String[]) projectSchemaConfig().getI18n().stream()
                    .map(I18NConfig::getBaseName)
                    .toArray();
        }
        return null;
    }

    /**
     * 获取指定名字的国际化配置信息
     *
     * @param baseName 名
     * @return i18n配置
     */
    public static I18NConfig getI18NConfig(String baseName) {
        if (StringUtils.isNotBlank(baseName) && enabled()) {
            return projectSchemaConfig().getI18n().stream()
                    .filter(x -> baseName.equals(x.getBaseName()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * 追加一个资源本地化，如果资源不存在则根据默认的Locale新建
     * <p>
     * 如果参数null则无效
     * <p>
     * 可修改资源的相对路径，也可以追加本地化
     *
     * @param baseName bundle资源名
     * @param locale   本地化
     * @param path     在工程中的相对路径
     */
    public static void addLocale(@NonNull String baseName, @NonNull Locale locale, @NonNull String path) {
        if (null == projectSchemaConfig().getI18n()) {
            projectSchemaConfig().setI18n(new HashSet<>());
        }

        boolean changed = false;
        // 指定消息资源的配置信息
        I18NConfig i18NConfig = getI18NConfig(baseName);
        boolean appended = (null == i18NConfig) || !i18NConfig.hasLocale(locale);

        // 追加新的locale需要从默认的复制过来
        // 否则只更新path即可
        // 追加新locale也要写入到project.json中
        if (appended) {
            // 资源文件的处理
            if (null == i18NConfig || i18NConfig.isEmptyLocaleNodes()) {
                // 建立新资源文件
                newDefaultFile(
                        FileUtil.mergePath(projectSchemaConfig().getProjectPath(), path),
                        baseName,
                        locale.toString());
            } else {
                // 复制文件
                newLocaleFile(
                        FileUtil.mergePath(projectSchemaConfig().getProjectPath(), path),
                        baseName,
                        locale.toString());
            }

            LocaleNode localeNode;
            if (null == i18NConfig) {
                // 全新的i18n资源配置
                localeNode = new LocaleNode(locale);
                i18NConfig = new I18NConfig();
                i18NConfig.setBaseName(baseName);
                i18NConfig.setProjectSchemaConfig(projectSchemaConfig());
                projectSchemaConfig().getI18n().add(i18NConfig);
            } else {
                // 已有i18n资源配置
                if (i18NConfig.isEmptyLocaleNodes()) {
                    // 没有配置任何本地化则新建LocaleNode
                    localeNode = new LocaleNode(locale);
                } else {
                    // 根据默认的locale复制LocaleNode
                    // 同时换成新的locale
                    localeNode = i18NConfig.getDefaultLocale().deepClone();
                    localeNode.setLocale(locale);
                }
            }

            i18NConfig.addLocaleNode(localeNode);
            changed = true;
        }

        if (!i18NConfig.getPath().equals(path)) {
            i18NConfig.setPath(path);
            changed = true;
        }

        if (changed) {
            // 增加了properties文件，所以更新工程全局的配置json
            new ProjectSchemaRepository().save(false);
        }
    }

    /**
     * 切换默认locale
     *
     * @param locale 默认的locale
     */
    public static void setDefaultLocale(@NonNull Locale locale) {
        if (!enabled())
            return;

        projectSchemaConfig().getI18n().forEach(i18NConfig -> {
            if (null != i18NConfig.getDefaultLocale() && !i18NConfig.getDefaultLocale().getLocale().equals(locale)) {
                LocaleNode localeNode = i18NConfig.getLocaleNode(locale);
                if (null != localeNode) {
                    i18NConfig.setDefaultLocale(localeNode);
                    flushDefaultFile(i18NConfig);
                }
            }
        });
    }

    /**
     * 获取指定资源下指定分类的国际化资源
     * <p>
     * 会尝试完全装载所有消息
     *
     * @param baseName 资源名
     * @param kind     分类名，即前缀
     * @return 具体的国际化资源
     * @throws SystemException 没有找到资源文件，或文件系统异常
     */
    public static Set<LocaleNode> getMessages(@NonNull String baseName, @NonNull String kind) {
        // 定位资源
        if (!enabled()) {
            return null;
        }

        I18NConfig i18NConfig = getI18NConfig(baseName);
        if (null == i18NConfig || i18NConfig.isEmptyLocaleNodes()) {
            return null;
        }

        Set<LocaleNode> result = new LinkedHashSet<>(i18NConfig.size());
        // 尝试加载所有的资源项
        loadAllMessages(i18NConfig, false);

        Iterator<LocaleNode> iterator = i18NConfig.getIterator();
        while (iterator.hasNext()) {
            LocaleNode localeNode = iterator.next();
            LocaleNode node = new LocaleNode(localeNode.getLocale());
            node.getItems().addAll(localeNode.getItems().stream()
                    .filter(x -> x.getKind().getPrefix().equals(kind))
                    .collect(Collectors.toSet()));
            result.add(node);
        }
        return result;
    }

    /**
     * 新的消息项，其他locale如果没有此key将会插入空值
     *
     * @param baseName 资源名
     * @param locale   本地化
     * @param kind     类别
     * @param key      key
     * @param value    值
     */
    public static void put(@NonNull String baseName, @NonNull Locale locale, @NonNull String kind, @NonNull String key, @NonNull String value) {

        I18NConfig i18NConfig = getI18NConfig(baseName);
        if (null == i18NConfig || i18NConfig.isEmptyLocaleNodes()) {
            return;
        }
        if (!i18NConfig.isFullLoad()) {
            loadAllMessages(i18NConfig, false);
        }

        MessageKind messageKind = i18NConfig.getKinds().stream()
                .filter(x -> x.getPrefix().equals(kind))
                .findFirst()
                .orElseThrow(() -> new ExceptionBuilder<>(SystemException.class)
                        .messageTemplate("The message kind \"{0}\" is not defined.")
                        .args(kind)
                        .build());

        MessageItem messageItem = new MessageItem();
        messageItem.setKey(key);
        messageItem.setKind(messageKind);

        // 循环所有locale
        LocaleNode localeNode = null;
        Iterator<LocaleNode> iterator = i18NConfig.getIterator();
        while (iterator.hasNext()) {
            localeNode = iterator.next();

            int seq = parseMessageKeySeq(messageKind, key);
            messageItem.setSeq(seq);
            messageKind.setMaxSequence(Math.max(messageKind.getMaxSequence(), seq));

            if (localeNode.getLocale().equals(locale)) {
                // 更新或新建当前locale的值
                messageItem.setValue(value);
                localeNode.getItems().add(messageItem);
            } else {
                // 其他locale如果没有此key则新建空值项目
                if (!localeNode.getItems().contains(messageItem)) {
                    messageItem.setValue(StringUtils.EMPTY);
                    localeNode.getItems().add(messageItem);
                }
            }
        }

        if (null != localeNode) {
            i18NConfig.setChanged(true);
        }
    }

    /**
     * 删除所有本地化文件中的消息项
     *
     * @param baseName 资源名
     * @param key      key
     */
    public static void remove(@NonNull String baseName, String key) {

        I18NConfig i18NConfig = getI18NConfig(baseName);
        if (null == i18NConfig || i18NConfig.isEmptyLocaleNodes()) {
            return;
        }
        if (!i18NConfig.isFullLoad()) {
            loadAllMessages(i18NConfig, false);
        }

        MessageItem messageItem = new MessageItem();
        messageItem.setKey(key);

        // 循环所有locale
        Iterator<LocaleNode> iterator = i18NConfig.getIterator();
        while (iterator.hasNext()) {
            LocaleNode localeNode = iterator.next();
            if (localeNode.getItems().remove(messageItem))
                i18NConfig.setChanged(true);
        }
    }

    public static void addKind(@NonNull String baseName, @NonNull MessageKind kind) {
        // 检查
        Assert.notNull(kind.getPrefix(), "Message kind prefix cannot be null.");
        // 检查是否存在
        I18NConfig i18NConfig = getI18NConfig(baseName);
        if (null == i18NConfig || i18NConfig.isEmptyLocaleNodes()) {
            return;
        }
        if (!i18NConfig.isFullLoad()) {
            loadAllMessages(i18NConfig, false);
        }

        if (i18NConfig.getKinds().contains(kind)) {
            return;
        }

        i18NConfig.getKinds().add(kind);
        i18NConfig.setChanged(true);
    }

    public static void removeKind(@NonNull String baseName, @NonNull String kind) {
        // 检查是否存在
        I18NConfig i18NConfig = getI18NConfig(baseName);
        if (null == i18NConfig || i18NConfig.isEmptyLocaleNodes()) {
            return;
        }
        if (!i18NConfig.isFullLoad()) {
            loadAllMessages(i18NConfig, false);
        }

        // 所有localeNode都要删除kind下的项目
        Iterator<LocaleNode> iterator = i18NConfig.getIterator();
        while (iterator.hasNext()) {
            LocaleNode localeNode = iterator.next();
            if (localeNode.getItems().removeIf(x -> x.getKind().getPrefix().equals(kind)))
                i18NConfig.setChanged(true);
        }

        MessageKind messageKind = new MessageKind();
        messageKind.setPrefix(kind);
        if (i18NConfig.getKinds().remove(messageKind))
            i18NConfig.setChanged(true);
    }

    /**
     * 重新加载文件并刷新状态
     *
     * @param baseName 资源名
     * @throws SystemException 文件没有找到，或读取文件失败
     */
    public static void reload(@NonNull String baseName) {
        I18NConfig i18NConfig = getI18NConfig(baseName);
        if (null == i18NConfig)
            return;

        // 清理一些属性
        i18NConfig.getKinds().clear();
        // 加载
        loadAllMessages(i18NConfig, true);
        i18NConfig.setChanged(false);
    }

    /**
     * 保存所有更改
     */
    public static void flush() {
        if (!enabled())
            return;

        // 一个文件一个线程来处理
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Object[]>> futureResult = new ArrayList<>();

        for (I18NConfig i18NConfig : projectSchemaConfig().getI18n()) {
            if (i18NConfig.isChanged() && i18NConfig.size() > 0) {
                // 开始保存文件
                // 首先以locale为单位
                Iterator<LocaleNode> iterator = i18NConfig.getIterator();
                while (iterator.hasNext()) {
                    LocaleNode localeNode = iterator.next();

                    futureResult.add(executorService.submit(() -> {
                        // 文件
                        try (RandomAccessFile file = new RandomAccessFile(
                                FileUtil.mergePath(
                                        projectSchemaConfig().getProjectPath(),
                                        i18NConfig.getPath(),
                                        i18NConfig.getBaseName() + "_" + localeNode.getLocaleString() + ".properties"),
                                "rw")
                        ) {
                            // 按kind来保存
                            for (MessageKind kind : i18NConfig.getKinds()) {
                                // 保存头
                                writeLine(file, StringUtils.leftPad(" Do not change this file!", 50, '#'));
                                writeLine(file, "# @prefix:" + kind.getPrefix());
                                writeLine(file, "# @description:" + kind.getDescription());
                                writeLine(file, "# @separator:" + kind.getSeparator());
                                writeLine(file, "# @rank:" + kind.getRank());
                                writeLine(file, "# @seq:" + kind.isSeqBoolean());
                                writeLine(file, "# @seqLength:" + kind.getSeqLength());

                                // 保存项目
                                Iterator<MessageItem> itemIterator = localeNode.getItems().stream()
                                        .filter(x -> x.getKind().equals(kind))
                                        .iterator();
                                while (itemIterator.hasNext()) {
                                    MessageItem item = itemIterator.next();
                                    writeLine(file, item.getKey() + '=' + item.getValue());
                                }
                            }
                        } catch (Exception e) {
                            return new Object[]{i18NConfig, e.getMessage()};
                        }
                        return new Object[]{i18NConfig, StringUtils.EMPTY};
                    }));
                }
            }
        }

        // 等待所有处理完成
        List<String> allError = new ArrayList<>();
        for (Future<Object[]> future : futureResult) {
            try {
                Object[] result = future.get();
                I18NConfig i18NConfig = (I18NConfig) (result[0]);
                String error = (String) result[1];
                if (StringUtils.isNotEmpty(error)) {
                    allError.add(i18NConfig.getBaseName() + " properties file save error: " + error);
                } else {
                    // 刷新修改标记并更新默认文件
                    i18NConfig.setChanged(false);
                    flushDefaultFile(i18NConfig);
                }
            } catch (Exception e) {
                allError.add(e.getMessage());
            }
        }
        executorService.shutdown();
        if (allError.size() > 0) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(Arrays.toString(allError.toArray()))
                    .throwIt();
        }
    }

    private static void writeLine(RandomAccessFile file, String line) throws IOException {
        file.write((line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建默认的空资源文件
     */
    private static void newDefaultFile(@NonNull String basePath, @NonNull String baseName, @NonNull String locale) {
        try {
            String s = FileUtil.mergePath(basePath, baseName);
            File file1 = new File(s + ".properties");
            File file2 = new File(s + "_" + locale + ".properties");
            file1.createNewFile();
            file2.createNewFile();
        } catch (IOException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }
    }

    /**
     * 刷新国际化的默认资源文件
     * <p>
     * 当默认的properties资源文件发生变化时调用
     * 或切换默认locale时调用
     *
     * @param i18NConfig 国际化配置
     * @throws SystemException 没有找到资源文件，或文件系统异常
     */
    private static void flushDefaultFile(@NonNull I18NConfig i18NConfig) {
        try {
            String s = FileUtil.mergePath(
                    projectSchemaConfig().getProjectPath(),
                    i18NConfig.getPath(),
                    i18NConfig.getBaseName());

            File source = new File(s + "_" + i18NConfig.getDefaultLocale().toString() + ".properties");
            File target = new File(s + ".properties");

            if (!source.exists()) {
                new ExceptionBuilder<>(SystemException.class)
                        .messageTemplate("Cannot found the default message resource file: {0}.")
                        .args(source.getPath())
                        .throwIt();
            }

            FileUtils.copyFile(
                    source,
                    target,
                    true,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }
    }

    /**
     * 以默认的文件为基准，复制一个新的本地化文件properties，如果目标文件存在直接覆盖。
     *
     * @param basePath 资源文件所在路径
     * @param baseName 资源名
     * @param locale   新的本地化
     * @throws SystemException 没有找到默认的资源文件，或文件系统异常
     */
    private static void newLocaleFile(@NonNull String basePath, @NonNull String baseName, @NonNull String locale) {
        try {
            String s = FileUtil.mergePath(basePath, baseName);

            File source = new File(s + ".properties");
            File target = new File(s + "_" + locale + ".properties");

            if (!source.exists()) {
                new ExceptionBuilder<>(SystemException.class)
                        .messageTemplate("Cannot found the default message resource file: {0}.")
                        .args(source.getPath())
                        .throwIt();
            }

            FileUtils.copyFile(
                    source,
                    target,
                    true,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }
    }

    private static int parseMessageKeySeq(@NonNull MessageKind kind, @NonNull String key) {
        if (!kind.isSeqBoolean())
            return 0;

        try {
            return Integer.parseInt(key.substring(kind.getPrefix().length() + kind.getSeparator().length()));
        } catch (Exception e) {
            throw new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("Message key sequence format error, cannot be converted to integer: prefix={0}, separator={1}, key={2}.")
                    .args(kind.getPrefix(), kind.getSeparator(), key)
                    .build();
        }
    }

    /**
     * 解析properties文件中的描述头行
     *
     * @param line 行文本
     * @return 名值对，解析失败（不是头信息）返回null
     */
    private static String[] parseHeader(String line) {
        // 分离字符串，去掉前面的#
        Iterable<String> iterable = Splitter.on(':')
                .trimResults()
                .limit(2)
                .split(line.substring(1));
        String[] result = new String[2];
        int i = 0;
        for (String s : iterable) {
            if (i == 0) {
                if (s.charAt(0) != '@')             // 不是头变量
                    return null;
                else
                    result[i++] = s.substring(1);   // 去掉头变量符号 @
            } else {
                result[i++] = s;
            }
        }
        // 不是名值对
        if (i != 2)
            return null;
        return result;
    }

    /**
     * 解析properties文件中的名值对
     *
     * @param line 行文本
     * @return 名值对，解析失败（不是名值对结构）返回null
     */
    private static String[] parseKeyValue(String line) {
        // 分离字符串，去掉前面的#
        Iterable<String> iterable = Splitter.on('=')
                .trimResults()
                .limit(2)
                .split(line);
        String[] result = new String[2];
        int i = 0;
        for (String s : iterable) {
            result[i++] = s;
        }
        // 不是名值对
        if (i != 2)
            return null;
        return result;
    }

    /**
     * 读取文件，加载所有资源
     *
     * @param i18NConfig 国际化资源配置
     * @param force      强制刷新
     * @throws SystemException 文件没有找到，或读取文件失败
     */
    private static void loadAllMessages(@NonNull I18NConfig i18NConfig, boolean force) {
        Iterator<LocaleNode> iterator = i18NConfig.getIterator();
        while (iterator.hasNext()) {
            LocaleNode node = iterator.next();
            loadMessages(i18NConfig, node, force);
        }
        i18NConfig.setFullLoad(true);
    }

    /**
     * 读取文件，加载所有资源
     *
     * @param i18NConfig 国际化资源配置
     * @param localeNode Locale
     * @param force      强制刷新
     * @throws SystemException 文件没有找到，或读取文件失败
     */
    private static void loadMessages(@NonNull I18NConfig i18NConfig, @NonNull LocaleNode localeNode, boolean force) {

        if (!force && CollectionUtils.isNotEmpty(localeNode.getItems())) {
            return;
        }

        String path = FileUtil.mergePath(
                projectSchemaConfig().getProjectPath(),
                i18NConfig.getPath());

        String filename = FileUtil.mergePath(
                path,
                i18NConfig.getBaseName() + "_" + localeNode.getLocaleString() + ".properties");

        File file = FileUtils.getFile(filename);
        if (!file.exists()) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File [{0}] not found!")
                    .args(filename)
                    .throwIt();
        }

        try {
            // 尝试修正文件换行符与系统一致
            FileUtil.correctLineSeparatorBySystem(filename);
            // 加载本地化文件
            loadMessagesFromFile(i18NConfig, filename, localeNode);
        } catch (IOException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }
    }

    /**
     * 装载本地化资源消息文件
     * <p>
     * 会自动装载消息类别
     *
     * @param i18NConfig 国际化资源配置
     * @param filename   资源文件
     * @param localeNode 本地化
     * @throws IOException 文件读取异常
     */
    private static void loadMessagesFromFile(@NonNull I18NConfig i18NConfig, @NonNull String filename, @NonNull LocaleNode localeNode) throws IOException {

        File file = new File(filename);
        try (LineIterator iterator = FileUtils.lineIterator(file)) {

            Set<MessageItem> items = new LinkedHashSet<>();
            // 缓存消息种类的头信息
            Map<String, String> kindTemp = new HashMap<>();
            boolean newKind = false;
            MessageKind messageKind = null;

            // 逐行读取文件
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                // 头信息或普通注释
                if (line.startsWith("#")) {
                    if (line.contains(":")) {
                        // 分析头信息 @key: value
                        String[] kv = parseHeader(line);
                        if (null == kv) {
                            continue;
                        }

                        if (!newKind) {
                            newKind = true;
                            kindTemp = new HashMap<>();
                        }
                        kindTemp.put(kv[0], kv[1]);
                    }
                    // 普通注释，忽略它
                    continue;
                }

                if (!line.contains("=")) {
                    continue;
                }

                // 将完整的kind信息保存进config
                if (newKind) {
                    // 检查是否完整
                    // prefix可以为空，但不能为null，且只能有一个是空
                    if (null == kindTemp.get("prefix")) {
                        new ExceptionBuilder<>(SystemException.class)
                                .messageTemplate("[{0}] format invalid.")
                                .args(filename)
                                .throwIt();
                    }
                    newKind = false;
                    // 定位消息种类，没有则新建一个种类并加入到配置实例中
                    Map<String, String> finalKindTemp = kindTemp;
                    messageKind = i18NConfig.getKinds().stream()
                            .filter(x -> x.getPrefix().equals(finalKindTemp.get("prefix")))
                            .findFirst()
                            .orElseGet(() -> {
                                MessageKind mk = new MessageKind();
                                mk.setPrefix(finalKindTemp.get("prefix"));
                                mk.setDescription(Optional
                                        .ofNullable(finalKindTemp.get("description"))
                                        .orElse(StringUtils.EMPTY));
                                mk.setRank(Integer.parseInt(Optional
                                        .ofNullable(finalKindTemp.get("rank"))
                                        .orElse("100")));
                                mk.setSeparator(Optional
                                        .ofNullable(finalKindTemp.get("separator"))
                                        .orElse("-"));
                                mk.setSeqBoolean(Boolean.parseBoolean(Optional
                                        .ofNullable(finalKindTemp.get("seq"))
                                        .orElse("true")));
                                mk.setSeqLength(Integer.parseInt(Optional
                                        .ofNullable(finalKindTemp.get("seqLength"))
                                        .orElse("4")));
                                i18NConfig.getKinds().add(mk);
                                return mk;
                            });
                }

                // 没有关联到消息种类上的忽略掉
                if (null == messageKind)
                    continue;

                // 分析当前行
                String[] kv = parseKeyValue(line);
                if (null == kv)
                    continue;

                // 尝试定位
                // 不允许重复
                MessageItem item = new MessageItem();
                item.setKey(kv[0]);
                if (items.contains(item)) {
                    new ExceptionBuilder<>(SystemException.class)
                            .messageTemplate("Duplicate property key [{0}].")
                            .args(kv[0])
                            .throwIt();
                }
                // 读取序号
                // 去掉前缀和分隔符
                if (messageKind.isSeqBoolean()) {
                    try {
                        item.setSeq(Integer.parseInt(
                                kv[0].substring(
                                        messageKind.getPrefix().length()
                                                + messageKind.getSeparator().length())));
                        if (messageKind.getMaxSequence() < item.getSeq())
                            messageKind.setMaxSequence(item.getSeq());
                    } catch (Exception ignore) {
                    }
                }
                item.setKind(messageKind);
                item.setValue(kv[1]);
                items.add(item);
            }
            // 刷新所有内容
            localeNode.getItems().clear();
            localeNode.getItems().addAll(items);
        }
    }

}
