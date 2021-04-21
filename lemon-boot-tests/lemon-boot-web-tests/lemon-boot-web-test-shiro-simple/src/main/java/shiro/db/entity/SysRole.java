package shiro.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
//@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

//    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色名称
     */
//    @TableField("name_")
    private String name;

    /**
     * 角色描述
     */
//    @TableField("description_")
    private String description;

    /**
     * 是否启用
     */
//    @TableField("active_")
    private Boolean active;

    /**
     * 创建时间
     */
//    @TableField(value = "create_time_", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 更新时间
     */
//    @TableField(value = "modified_time_", fill = FieldFill.INSERT_UPDATE)
    private Timestamp modifiedTime;

    /**
     * 乐观锁
     */
//    @TableField("rev_")
//    @Version
    private Long rev;

    /**
     * 逻辑删除
     */
//    @TableField("deleted_")
//    @TableLogic
    private Boolean deleted;

//    @TableField("tenant_")
    private Long tenant;


}
