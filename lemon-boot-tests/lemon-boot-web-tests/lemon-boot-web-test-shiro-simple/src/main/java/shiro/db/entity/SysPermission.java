package shiro.db.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_permission")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 乐观锁
     */
    @TableField("rev_")
    @Version
    private Long rev;

    @TableField("tenant_")
    private Long tenant;

    /**
     * 角色ID
     */
    @TableField("role_id_")
    private Long roleId;

    /**
     * 权限
     */
    @TableField("permission_")
    private String permission;

    /**
     * 创建时间
     */
    @TableField(value = "create_time_", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @TableField(value = "modified_time_", fill = FieldFill.INSERT_UPDATE)
    private Timestamp modifiedTime;

    /**
     * 逻辑删除
     */
    @TableField("deleted_")
    @TableLogic
    private Boolean deleted;

}
