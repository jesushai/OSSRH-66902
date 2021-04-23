package com.lemon.framework.auth.shiro;

import com.lemon.framework.util.LoggerUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.cache.support.NullValue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ShiroSession extends SimpleSession {

    static int bitIndexCounter = 0;
    private static final int ID_BIT_MASK;
    private static final int START_TIMESTAMP_BIT_MASK;
    private static final int STOP_TIMESTAMP_BIT_MASK;
    private static final int LAST_ACCESS_TIME_BIT_MASK;
    private static final int TIMEOUT_BIT_MASK;
    private static final int EXPIRED_BIT_MASK;
    private static final int EXPIRE_TIMESTAMP_BIT_MASK;
    private static final int HOST_BIT_MASK;
    private static final int ATTRIBUTES_BIT_MASK;

    public static final String IS_ANON = "IS_ANON";

    /**
     * 当前过期的时间节点
     */
    private transient Date expireTimestamp;

    public ShiroSession() {
        Date now = new Date();
        super.setStartTimestamp(now);
        super.setLastAccessTime(now);
        super.setTimeout(AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT);
        super.setHost("");
        this.expireTimestamp = new Date(now.getTime() + getTimeout());
    }

    public ShiroSession(String host) {
        this();
        super.setHost(host);
    }

    public ShiroSession(Serializable id, Date startTimestamp, Date lastAccessTime, long timeout, String host, Date expireTimestamp) {
        super.setId(id);
        Date now = new Date();
        super.setStartTimestamp(null == startTimestamp ? now : startTimestamp);
        super.setLastAccessTime(null == lastAccessTime ? now : lastAccessTime);
        super.setTimeout(timeout > 0 ? timeout : AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT);
        super.setHost(null == host ? "" : host);
        this.expireTimestamp = null == expireTimestamp ? new Date(getStartTimestamp().getTime() + timeout) : expireTimestamp;
    }

    /**
     * touch不会刷新lastAccessTime（屏蔽了父类方法）
     */
    @Override
    public void touch() {
//        super.touch();
    }

    /**
     * 手动指定touch的时间
     *
     * @param time 触发时间
     */
    public void touch(Date time) {
        LoggerUtils.debug(log, "Touch session: id={}, lastAccessTime={}", getId(), time);
        super.setLastAccessTime(time);
        touch();
    }

    /**
     * 获取属性，对Null值特殊处理
     *
     * @param key 属性Key
     * @return 属性值
     */
    @Override
    public Object getAttribute(Object key) {
        Object value = super.getAttribute(key);
        if (NullValue.INSTANCE.equals(value)) {
            return null;
        } else {
            return value;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        short alteredFieldsBitMask = this.getAlteredFieldsBitMask();
        out.writeShort(alteredFieldsBitMask);
        if (this.getId() != null) {
            out.writeObject(this.getId());
        }

        if (super.getStartTimestamp() != null) {
            out.writeObject(super.getStartTimestamp());
        }

        if (super.getStopTimestamp() != null) {
            out.writeObject(super.getStopTimestamp());
        }

        if (super.getLastAccessTime() != null) {
            out.writeObject(super.getLastAccessTime());
        }

        if (super.getTimeout() != 0L) {
            out.writeLong(super.getTimeout());
        }

        if (super.isExpired()) {
            out.writeBoolean(super.isExpired());
        }

        if (expireTimestamp != null) {
            out.writeObject(expireTimestamp);
        }

        if (super.getHost() != null) {
            out.writeUTF(super.getHost());
        }

        if (!CollectionUtils.isEmpty(super.getAttributes())) {
            out.writeObject(super.getAttributes());
        }

    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        short bitMask = in.readShort();
        if (isFieldPresent(bitMask, ID_BIT_MASK)) {
            super.setId((Serializable) in.readObject());
        }

        if (isFieldPresent(bitMask, START_TIMESTAMP_BIT_MASK)) {
            super.setStartTimestamp((Date) in.readObject());
        }

        if (isFieldPresent(bitMask, STOP_TIMESTAMP_BIT_MASK)) {
            super.setStopTimestamp((Date) in.readObject());
        }

        if (isFieldPresent(bitMask, LAST_ACCESS_TIME_BIT_MASK)) {
            super.setLastAccessTime((Date) in.readObject());
        }

        if (isFieldPresent(bitMask, TIMEOUT_BIT_MASK)) {
            super.setTimeout(in.readLong());
        }

        if (isFieldPresent(bitMask, EXPIRED_BIT_MASK)) {
            super.setExpired(in.readBoolean());
        }

        if (isFieldPresent(bitMask, EXPIRE_TIMESTAMP_BIT_MASK)) {
            expireTimestamp = (Date) in.readObject();
        }

        if (isFieldPresent(bitMask, HOST_BIT_MASK)) {
            super.setHost(in.readUTF());
        }

        if (isFieldPresent(bitMask, ATTRIBUTES_BIT_MASK)) {
            super.setAttributes((Map) in.readObject());
        }

    }

    private short getAlteredFieldsBitMask() {
        int bitMask = 0;
        bitMask = super.getId() != null ? bitMask | ID_BIT_MASK : bitMask;
        bitMask = super.getStartTimestamp() != null ? bitMask | START_TIMESTAMP_BIT_MASK : bitMask;
        bitMask = super.getStopTimestamp() != null ? bitMask | STOP_TIMESTAMP_BIT_MASK : bitMask;
        bitMask = super.getLastAccessTime() != null ? bitMask | LAST_ACCESS_TIME_BIT_MASK : bitMask;
        bitMask = super.getTimeout() != 0L ? bitMask | TIMEOUT_BIT_MASK : bitMask;
        bitMask = super.isExpired() ? bitMask | EXPIRED_BIT_MASK : bitMask;
        bitMask = expireTimestamp != null ? bitMask | EXPIRE_TIMESTAMP_BIT_MASK : bitMask;
        bitMask = super.getHost() != null ? bitMask | HOST_BIT_MASK : bitMask;
        bitMask = !CollectionUtils.isEmpty(super.getAttributes()) ? bitMask | ATTRIBUTES_BIT_MASK : bitMask;
        return (short) bitMask;
    }

    private static boolean isFieldPresent(short bitMask, int fieldBitMask) {
        return (bitMask & fieldBitMask) != 0;
    }

    static {
        ID_BIT_MASK = 1 << bitIndexCounter++;
        START_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
        STOP_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
        LAST_ACCESS_TIME_BIT_MASK = 1 << bitIndexCounter++;
        TIMEOUT_BIT_MASK = 1 << bitIndexCounter++;
        EXPIRED_BIT_MASK = 1 << bitIndexCounter++;
        EXPIRE_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
        HOST_BIT_MASK = 1 << bitIndexCounter++;
        ATTRIBUTES_BIT_MASK = 1 << bitIndexCounter++;
    }
}
