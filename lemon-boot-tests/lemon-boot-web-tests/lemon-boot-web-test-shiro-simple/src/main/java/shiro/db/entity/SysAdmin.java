package shiro.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.framework.auth.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
//@TableName(value = "sys_admin", autoResultMap = true)
public class SysAdmin implements Serializable, User {

    private static final long serialVersionUID = 1L;

    /**
     * 用户输入的密码明文
     */
//    @TableField(exist = false)
    private String rawPassword;

//    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 乐观锁
     */
//    @TableField("rev_")
//    @Version
    private Long rev;

    /**
     * 所属租户
     */
//    @TableField("tenant_")
    private Long tenant;

    /**
     * 姓名
     */
//    @TableField("display_")
    private String display;

    /**
     * 登录名
     */
//    @TableField("username_")
    private String username;

    /**
     * 加密的密码
     */
    @JsonIgnore
//    @TableField("password_")
    private String password;

    /**
     * 最后登录IP
     */
//    @TableField("last_ip_")
    private String lastIp;

    /**
     * 最后登录时间
     */
//    @TableField("last_login_")
    private Timestamp lastLogin;

    /**
     * 头像
     */
//    @TableField("avatar_")
    private String avatar;

    /**
     * 角色列表json数组
     */
//    @TableField(value = "role_ids_")
    private Long[] roleIds;

    /**
     * 电话
     */
//    @TableField("phone_")
    private String phone;

    /**
     * 身份证
     */
//    @TableField("identity_")
    private String identity;

//    @TableField("email_")
    private String email;

    /**
     * 微信id
     */
//    @TableField("wechat_")
    private String wechat;

    /**
     * 是否可用
     */
//    @TableField("active_")
    private Boolean active;

    /**
     * 删除标记
     */
//    @TableField("deleted_")
//    @TableLogic
    private Boolean deleted;

    /**
     * 描述
     */
//    @TableField("description_")
    private String description;

    /**
     * 创建人
     */
//    @TableField(value = "create_by_", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建人姓名
     */
//    @TableField(value = "create_name_by_", fill = FieldFill.INSERT)
    private String createNameBy;

    /**
     * 创建时间
     */
//    @TableField(value = "create_time_", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 最后修改人
     */
//    @TableField(value = "modified_by_", fill = FieldFill.INSERT_UPDATE)
    private Long modifiedBy;

    /**
     * 最后修改人名
     */
//    @TableField(value = "modified_name_by_", fill = FieldFill.INSERT_UPDATE)
    private String modifiedNameBy;

    /**
     * 最后修改时间
     */
//    @TableField(value = "modified_time_", fill = FieldFill.INSERT_UPDATE)
    private Timestamp modifiedTime;

    @Override
    public boolean isValid() {
        return this.active && !this.deleted;
    }

}
