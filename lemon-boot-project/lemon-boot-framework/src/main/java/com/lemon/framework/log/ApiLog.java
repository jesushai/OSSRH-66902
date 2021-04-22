package com.lemon.framework.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 名称：业务服务日志<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Data
@EqualsAndHashCode
public class ApiLog implements Log {

    private static final long serialVersionUID = 3323405329126080041L;

    /**
     * 日志类型：系统服务日志|用户行为日志|SQL日志|异常日志<br/>
     */
    private String type;

    /**
     * 业务名称：中文<br/>
     */
    private String bizName;

    /**
     * 租户ID
     */
    private Long tenant;

    /**
     * 业务操作对应的资源类型，或者表名
     */
    private String resourceType;

    /**
     * 业务操作的资源主键
     */
    private Long resourceId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 操作员ID
     */
    private Long userId;

    /**
     * 操作员名
     */
    private String username;

    /**
     * 操作的IP
     */
    private String ip;

    /**
     * 日志产生的时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date datetime;

    /**
     * 日志描述文本
     */
    private String description;

    /**
     * 耗时时间（毫秒）
     */
    private Long spend;

    public <T extends ApiLog> T copyTo(Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            t.setBizName(this.bizName);
            t.setDatetime(this.datetime);
            t.setDescription(this.description);
            t.setIp(this.ip);
            t.setResourceId(this.resourceId);
            t.setResourceType(this.resourceType);
            t.setSessionId(this.sessionId);
            t.setSpend(this.spend);
            t.setTenant(this.tenant);
            t.setType(this.type);
            t.setUserId(this.userId);
            t.setUsername(this.username);
            return t;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot copy object: " + e.getMessage());
        }
    }

}
