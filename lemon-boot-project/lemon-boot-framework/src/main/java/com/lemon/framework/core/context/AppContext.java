package com.lemon.framework.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
public class AppContext {

    private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();

    /**
     * 附件，会在服务间传递
     */
    private final ConcurrentHashMap<String, String> attachments = new ConcurrentHashMap<>();

    public Object get(String key) {
        return values.get(checkKey(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object object = get(checkKey(key));
        return (object != null && clazz.isAssignableFrom(object.getClass())) ? (T) object : null;
    }

    public AppContext set(String key, Object value) {
        String k = checkKey(key);
        if (value == null) {
            values.remove(k);
        } else {
            values.put(k, value);
        }
        return this;
    }

    public AppContext remove(String key) {
        values.remove(checkKey(key));
        return this;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public String getAttachment(String key) {
        return attachments.get(checkKey(key));
    }

    public AppContext setAttachment(String key, String value) {
        String k = checkKey(key);
        if (value == null) {
            attachments.remove(k);
        } else {
            attachments.put(k, value);
        }
        return this;
    }

    public AppContext removeAttachment(String key) {
        attachments.remove(checkKey(key));
        return this;
    }

    public AppContext setAttachments(Map<String, String> attachment) {
        this.attachments.clear();
        if (attachment != null && attachment.size() > 0) {
            attachment.keySet().forEach(k -> {
                attachments.put(k.toUpperCase(), attachment.get(k));
            });
        }
        return this;
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    public void clear() {
        this.values.clear();
        this.attachments.clear();
    }

    private String checkKey(String key) {
        if (null == key || "".equals(key))
            throw new RuntimeException("AppContext: Key cannot be empty.");
        return key;
    }

}
