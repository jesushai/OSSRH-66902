package shiro.db.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lemon.framework.auth.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import shiro.db.handler.JsonLongArrayTypeHandler;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_admin", autoResultMap = true)
public class SysAdmin implements Serializable, User {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 管理员姓名
     */
    @TableField("username_")
    private String username;

    /**
     * 管理员密码
     */
    @TableField("password_")
    private String password;

    /**
     * 最近一次登录IP地址
     */
    @TableField("last_login_ip_")
    private String lastLoginIp;

    /**
     * 最近一次登录时间
     */
    @TableField("last_login_time_")
    private Timestamp lastLoginTime;

    /**
     * 头像图片
     */
    @TableField("avatar_")
    private String avatar;

    /**
     * 创建时间
     */
    @TableField(value = "add_time_", fill = FieldFill.INSERT)
    private Timestamp addTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time_", fill = FieldFill.INSERT_UPDATE)
    @Version
    private Timestamp updateTime;

    /**
     * 逻辑删除
     */
    @TableField("deleted_")
    @TableLogic
    private Boolean deleted;

    /**
     * 角色列表json数组
     */
    @TableField(value = "role_ids_", typeHandler = JsonLongArrayTypeHandler.class)
    private Long[] roleIds;

    @TableField("tenant_")
    private Long tenant;


    @Override
    public boolean isValid() {
        return !deleted;
    }
}
