package com.lemon.schemaql.config.support.i18n;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.KeyValue;

/**
 * 名称：国际化消息项目<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/30
 */
@Data
@Accessors
@EqualsAndHashCode(of = "key")
@ToString
public class MessageItem implements KeyValue<String, String>, Cloneable {

    /**
     * 完整的key
     */
    private String key;

    private String value;

    /**
     * 序列，MessageKind.seqBoolean=false则忽略
     */
    private int seq;

    /**
     * 当前消息属于哪个类
     */
    private MessageKind kind;

    public MessageItem clone(MessageKind kind) {
        MessageItem newObject = null;
        try {
            newObject = (MessageItem) this.clone();
        } catch (CloneNotSupportedException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }
        newObject.kind = kind;
        return newObject;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}