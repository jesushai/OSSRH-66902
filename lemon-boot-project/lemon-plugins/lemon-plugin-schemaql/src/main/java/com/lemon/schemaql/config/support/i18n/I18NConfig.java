package com.lemon.schemaql.config.support.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.schemaql.config.ProjectSchemaConfig;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.lang.NonNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;

/**
 * 名称：国际化配置类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/28
 */
@EqualsAndHashCode(of = "baseName")
public class I18NConfig {

    /**
     * 国际化资源路径，相对于projectPath
     */
    private String path;

    /**
     * 国际化资源名
     */
    private String baseName;

    /**
     * 用于装载json文件
     */
    private LinkedHashSet<String> locales;

    /**
     * 消息类别
     */
    @JsonIgnore
    private final LinkedHashSet<MessageKind> kinds = new LinkedHashSet<>();

    /**
     * 缓存所有本地化消息
     */
    @JsonIgnore
    private final LinkedHashSet<LocaleNode> localeNodes = new LinkedHashSet<>();

    /**
     * 默认的本地化引用
     */
    @JsonIgnore
    private LocaleNode defaultLocale = null;

    @JsonIgnore
    private ProjectSchemaConfig projectSchemaConfig;

    @JsonIgnore
    private boolean changed = false;

    @JsonIgnore
    private boolean fullLoad = false;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isFullLoad() {
        return fullLoad;
    }

    public void setFullLoad(boolean fullLoad) {
        this.fullLoad = fullLoad;
    }

    public Iterator<LocaleNode> getIterator() {
        return localeNodes.iterator();
    }

    public int size() {
        return localeNodes.size();
    }

    public LocaleNode getLocaleNode(@NonNull String locale) {
        return localeNodes.stream()
                .filter(x -> x.getLocaleString().equals(locale))
                .findFirst()
                .orElse(null);
    }

    public LocaleNode getLocaleNode(@NonNull Locale locale) {
        return localeNodes.stream()
                .filter(x -> x.getLocale().equals(locale))
                .findFirst()
                .orElse(null);
    }

    public boolean hasLocale(@NonNull Locale locale) {
        return localeNodes.stream().anyMatch(x -> x.getLocale().equals(locale));
    }

    public boolean isEmptyLocaleNodes() {
        return localeNodes.size() <= 0;
    }

    /**
     * 增加本地化，如果是第一个则直接设为默认locale
     *
     * @param node localeNode
     */
    public void addLocaleNode(@NonNull LocaleNode node) {
        localeNodes.add(node);
        if (null == locales)
            locales = new LinkedHashSet<>();
        locales.add(node.getLocaleString());
        if (localeNodes.size() == 1) {
            defaultLocale = node;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public LinkedHashSet<String> getLocales() {
        return locales;
    }

    public void setLocales(LinkedHashSet<String> locales) {
        this.locales = locales;
    }

    public LinkedHashSet<MessageKind> getKinds() {
        return kinds;
    }

    public LocaleNode getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * 设置默认的locale，如果默认的locale不在locale列表中则抛出异常
     *
     * @param defaultLocale 默认的locale
     * @throws SystemException 默认的locale不在locale列表中
     */
    public void setDefaultLocale(LocaleNode defaultLocale) {
        LocaleNode node = localeNodes.stream()
                .filter(x -> x.getLocaleString().equals(defaultLocale.getLocaleString()))
                .findFirst()
                .orElse(null);
        if (null == node) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("The locale list not contained [{0}]")
                    .args(defaultLocale.getLocaleString())
                    .throwIt();
        }
        this.defaultLocale = node;
    }

    public ProjectSchemaConfig getProjectSchemaConfig() {
        return projectSchemaConfig;
    }

    public void setProjectSchemaConfig(@NonNull ProjectSchemaConfig projectSchemaConfig) {
        this.projectSchemaConfig = projectSchemaConfig;
        // 由json文件而来的locales列表，这里需要转成LocaleNode，因为后续的资源都以LocaleNode为根本。
        if (localeNodes.size() <= 0 && null != locales && locales.size() > 0) {
            for (String locale : locales) {
                try {
                    LocaleNode localeNode = new LocaleNode(LocaleUtils.toLocale(locale));
                    localeNodes.add(localeNode);
                    // 自动根据项目全局配置，来设置当前国际化资源的默认locale
                    if (locale.equals(projectSchemaConfig.getI18nDefault())) {
                        defaultLocale = localeNode;
                    }
                } catch (IllegalArgumentException ignore) {
                    // locale文本是错误的
                }
            }
            // 项目配置中没有默认，则使用第一个作为默认
            if (null == defaultLocale && localeNodes.size() > 0) {
                defaultLocale = localeNodes.stream().findFirst().orElse(null);
                projectSchemaConfig.setI18nDefault(defaultLocale.getLocaleString());
            }
        }
    }

}
