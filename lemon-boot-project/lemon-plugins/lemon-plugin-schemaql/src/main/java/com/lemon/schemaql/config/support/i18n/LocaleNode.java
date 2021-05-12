package com.lemon.schemaql.config.support.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * 名称：国际化消息的本地化节点<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/30
 */
@ToString(of = {"locale"})
@EqualsAndHashCode(of = "localeString")
public class LocaleNode implements Cloneable {

    public LocaleNode() {
        this.items = new LinkedHashSet<>();
    }

    public LocaleNode(@NonNull Locale locale) {
        this.locale = locale;
        this.localeString = locale.toString();
        this.items = new LinkedHashSet<>();
    }

    /**
     * 具体国家语言
     */
    @JsonIgnore
    private Locale locale;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(@NonNull Locale locale) {
        this.locale = locale;
        this.localeString = locale.toString();
    }

    /**
     * 具体国家语言的文本表达
     */
    private String localeString;

    public String getLocaleString() {
        return localeString;
    }

    /**
     * 本地化包含的所有消息项
     */
    @JsonIgnore
    private Set<MessageItem> items;

    public Set<MessageItem> getItems() {
        return items;
    }

    /**
     * 深度克隆
     *
     * @return 新的本地化节点对象
     */
    public LocaleNode deepClone() {
        LocaleNode newObject = null;
        try {
            newObject = (LocaleNode) this.clone();
        } catch (CloneNotSupportedException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }

        // 深度克隆
        int size = this.items.size();
        newObject.items = new LinkedHashSet<>(size);

        Iterator<MessageItem> si = this.items.iterator();
        for (int i = 0; i < size; i++) {
            newObject.items.add(si.next().deepClone());
        }

        return newObject;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
