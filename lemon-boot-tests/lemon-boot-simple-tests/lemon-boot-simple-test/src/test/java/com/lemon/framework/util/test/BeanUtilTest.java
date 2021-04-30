package com.lemon.framework.util.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/29
 */
class BeanUtilTest {

    @Test
    void cloneMessageKind() {
        MessageKind k1 = new MessageKind("des", 1, "pre-", "-", false, new LinkedHashSet<>());
        k1.getMessages().add(new KeyValuePair("k1", 1, new LinkedList<String>() {{add("1"); add("2");}}, 0));
        try {
            MessageKind k2 = (MessageKind) k1.clone();
            k2.setDescription("ddd222");
            KeyValuePair keyValuePair = k2.getMessages().stream()
                    .findFirst()
                    .orElse(null);
            if (null != keyValuePair) {
                keyValuePair.setSeq(22);
                keyValuePair.getValue().add("3");
            }
            System.out.println(k1);
            System.out.println(k2);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(of = "prefix")
    @ToString
    public static class MessageKind implements Cloneable {

        /**
         * 描述
         */
        private String description;

        /**
         * 顺序
         */
        private int rank;

        /**
         * key的前缀
         */
        private String prefix;

        /**
         * 前缀与具体内容的分隔符，默认是-
         */
        private String separator = "-";

        /**
         * 是否按序列排号，默认true
         */
        private boolean seqBoolean = true;

        /**
         * 具体的国际化消息
         */
        private LinkedHashSet<KeyValuePair> messages;

        @Override
        public Object clone() throws CloneNotSupportedException {
            MessageKind clone = (MessageKind) super.clone();
            if (null != messages) {
                LinkedHashSet<KeyValuePair> newMessages = new LinkedHashSet<>(messages.size());
                for (KeyValuePair message : messages) {
                    newMessages.add((KeyValuePair) message.clone());
                }
                clone.setMessages(newMessages);
            }
            return clone;
        }
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(of = "key")
    @ToString
    public static class KeyValuePair implements Cloneable {

        /**
         * 完整的key
         */
        private String key;

        /**
         * 序列
         */
        private int seq;

        /**
         * 按照本地化顺序存储的值
         */
        private LinkedList<String> value;

        /**
         * 改变标记：0未改变；1改变（新增或删除）；-1删除；
         */
        private int changedFlag;

        @Override
        public Object clone() throws CloneNotSupportedException {
            KeyValuePair clone = (KeyValuePair) super.clone();
            clone.setValue(new LinkedList<>(value));
            return clone;
        }
    }
}
