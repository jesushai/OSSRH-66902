package com.lemon.schemaql.engine.helper;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.ProjectSchemaConfig;
import com.lemon.schemaql.config.support.i18n.I18NConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/28
 */
public class I18NHelper {

    private static Set<I18NConfig> i18NConfig;

    public static void setI18NConfig(Set<I18NConfig> i18NConfig) {
        I18NHelper.i18NConfig = i18NConfig;
    }

    public static boolean enabled() {
        return null != i18NConfig && i18NConfig.size() > 0;
    }

    /**
     * 获取国际化资源的基础Bundle名
     *
     * @return Bundle名
     */
    public static String[] getAllResourceBaseName() {
        if (enabled()) {
            return (String[]) i18NConfig.stream()
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
    public static I18NConfig getConfig(String baseName) {
        if (StringUtils.isNotBlank(baseName) && enabled()) {
            return i18NConfig.stream()
                    .filter(x -> baseName.equals(x.getBaseName()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * 追加一个资源本地化，如果资源不存在则新建
     * <p>
     * 如果参数null则无效
     * <p>
     * 可修改资源的相对路径，也可以追加本地化
     *
     * @param baseName            bundle资源名
     * @param locale              本地化
     * @param path                在工程中的相对路径
     * @param projectSchemaConfig 工程配置
     */
    public static void addLocale(String baseName, Locale locale, String path, ProjectSchemaConfig projectSchemaConfig) {
        if (null == baseName || null == locale) {
            return;
        }

        if (null == i18NConfig) {
            i18NConfig = new HashSet<>();
            projectSchemaConfig.setI18n(i18NConfig);
        }

        boolean appended = false;

        I18NConfig config = i18NConfig.stream()
                .filter(x -> baseName.equals(x.getBaseName()))
                .findFirst()
                .orElse(null);

        if (null == config) {
            config = new I18NConfig()
                    .setPath(path)
                    .setBaseName(baseName)
                    .setProjectSchemaConfig(projectSchemaConfig);

            config.getLocales().add(locale.toString());
            i18NConfig.add(config);

            appended = true;
        } else {
            config.setPath(path);
            if (!config.getLocales().contains(locale.toString())) {
                config.getLocales().add(locale.toString());
                appended = true;
            }
        }

        // 追加新的locale需要从默认的复制过来
        if (appended) {
            // 复制文件
            // 以默认的文件为基准，如果目标文件存在直接覆盖！
            try {
                String s = FileUtil.mergePath(
                        projectSchemaConfig.getProjectPath(),
                        path,
                        baseName);

                File source = new File(s + ".properties");
                File target = new File(s + "_" + locale.toString() + ".properties");

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

            // TODO: 复制缓存
        }
    }

    /**
     * 获取指定资源下指定分类的国际化资源
     *
     * @param baseName 资源名
     * @param kind     分类名
     * @return 具体的国际化资源
     */
    public static Set<I18NConfig.KeyValuePair> getMessages(String baseName, String kind) {
        // 定位资源
        if (StringUtils.isBlank(baseName) || !enabled()) {
            return null;
        }

        I18NConfig config = i18NConfig.stream()
                .filter(x -> baseName.equals(x.getBaseName()))
                .findFirst()
                .orElse(null);
        if (null == config) {
            return null;
        }

        // 已经缓存
        I18NConfig.MessageKind mk = config.getKinds().stream()
                .filter(x -> x.getPrefix().equals(kind))
                .findFirst()
                .orElse(null);
        if (null != mk) {
            return mk.getMessages();
        }

        // 没有缓存则直接读取
        readAllData(config, baseName, kind);

        // 读取文件


        // 定位分类
        // 获取每一行，直到结尾或下一分类
        return null;
    }

    public static void put(String baseName, String kind, String key, String value) {

    }

    private static void readAllData(I18NConfig config, String baseName, String kind) {

        String path = FileUtil.mergePath(
                config.getProjectSchemaConfig().getProjectPath(),
                config.getPath());

        for (String locale : config.getLocales()) {
            String filename = FileUtil.mergePath(path, baseName + "_" + locale + ".properties");
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
            } catch (IOException e) {
            }
        }
    }

    private static void loadMessages(I18NConfig config, String filename, String locale) throws IOException {
        int localeIndex = config.getLocales().indexOf(locale);

        File file = new File(filename);
        try (LineIterator iterator = FileUtils.lineIterator(file)) {

            // 缓存消息种类的头信息
            Map<String, String> kindTemp = new HashMap<>();
            boolean newKind = false;
            I18NConfig.MessageKind messageKind = null;

            // 逐行读取文件
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                // 头信息或普通注释
                if (line.startsWith("#")) {
                    if (line.contains(":")) {
                        if (!newKind) {
                            newKind = true;
                            kindTemp = new HashMap<>();
                        }
                        // 分析头信息
                        String[] kv = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                                line.substring(1), ":", 2);
                        kindTemp.put(kv[0], kv[1]);
                        continue;
                    } else {
                        // 普通注释，忽略它
                        continue;
                    }
                }

                // 将完整的kind信息保存进config
                if (newKind) {
                    // 检查是否完整
                    // prefix可以为空，但不能为null，且只能有一个是空
                    if (null == kindTemp.get("prefix")) {
                        new ExceptionBuilder<>(SystemException.class)
                                .messageTemplate("[" + filename + "] format invalid.")
                                .throwIt();
                    }
                    newKind = false;
                    // 定位消息种类，没有则新建一个种类并加入到配置实例中
                    Map<String, String> finalKindTemp = kindTemp;
                    messageKind = config.getKinds().stream()
                            .filter(x -> x.getPrefix().equals(finalKindTemp.get("prefix")))
                            .findFirst()
                            .orElseGet(() -> {
                                I18NConfig.MessageKind mk = new I18NConfig.MessageKind();
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
                                config.getKinds().add(mk);
                                return mk;
                            });
                }

                // 没有关联到消息种类上的忽略掉
                if (null == messageKind)
                    continue;

                // 分析当前行
                String[] kv = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                        line.substring(1), "=", 2);
                // 尝试定位
                I18NConfig.MessageKind finalMessageKind = messageKind;
                I18NConfig.KeyValuePair pair = finalMessageKind.getMessages().stream()
                        .filter(x -> x.getKey().equals(kv[0]))
                        .findFirst()
                        .orElseGet(() -> {
                            // 检查是否重复
                            if (config.getAllMessages().contains(kvp)) {
                                new ExceptionBuilder<>(SystemException.class)
                                        .messageTemplate("Duplicate property key [{}].")
                                        .args(kv[0])
                                        .throwIt();
                            }
                            // 新建并赋值
                            I18NConfig.KeyValuePair kvp = new I18NConfig.KeyValuePair();
                            kvp.setKey(kv[0]);
                            if (finalMessageKind.isSeqBoolean()) {
                                // 去掉前缀和分隔符
                                try {
                                    kvp.setSeq(Integer.parseInt(
                                            kv[0].substring(
                                                    finalMessageKind.getPrefix().length()
                                                            + finalMessageKind.getSeparator().length())));
                                } catch (Exception ignore) {
                                }
                            }
                            kvp.setChangedFlag(0);
                            kvp.setKind(finalMessageKind);
                            // 缓存进大列表中
                            config.getAllMessages().add(kvp);
                            return kvp;
                        });
                // 增加新的locale值
                if (pair.getValue().size() >= localeIndex) {
                    // 不应该出现这种现象
                    new ExceptionBuilder<>(SystemException.class)
                            .messageTemplate("Messages resource is not unique in locale.")
                            .throwIt();
                }

                pair.getValue().addLast(kv[1]);
            }
        }
    }
}
