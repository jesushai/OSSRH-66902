package shiro.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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

    @TableField("tenant_")
    private Long tenant;


}
