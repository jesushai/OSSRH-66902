package com.lemon.schemaql.config.support.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.schemaql.config.ProjectSchemaConfig;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * 名称：国际化配置类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/28
 */
@Data
@Accessors(chain = true)
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
     * 本地化列表，第一个就是默认的
     */
    private LinkedList<String> locales = new LinkedList<>();


    @JsonIgnore
    private ProjectSchemaConfig projectSchemaConfig;

    @JsonIgnore
    private LinkedHashSet<MessageKind> kinds = new LinkedHashSet<>();

    /**
     * 缓存全部的国际化消息，与kinds内的是一个引用，不会占用多一倍的内存
     */
    @JsonIgnore
    private Set<KeyValuePair> allMessages = new HashSet<>();

    @Override
    public Object clone() throws CloneNotSupportedException {
        I18NConfig clone = (I18NConfig) super.clone();
        if (null != kinds) {
            LinkedHashSet<MessageKind> newKinds = new LinkedHashSet<>(kinds.size());
            for (MessageKind kind : kinds) {
                newKinds.add((MessageKind) kind.clone());
            }
            clone.setKinds(newKinds);
        }
        return clone;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(of = "prefix")
    @ToString
    public static class MessageKind implements Cloneable {

        /**
         * 具体的国际化消息
         */
        private LinkedHashSet<KeyValuePair> messages = new LinkedHashSet<>();

    }

}
