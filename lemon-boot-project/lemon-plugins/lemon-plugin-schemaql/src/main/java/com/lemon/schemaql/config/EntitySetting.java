package com.lemon.schemaql.config;

import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * 名称：全局的实体配置<p>
 * 描述：<p>
 * 一个工程应该统一这些信息
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
@Data
@Accessors(chain = true)
public class EntitySetting {
    /**
     * 实体ID的类型
     */
    private IdType idType = IdType.ASSIGN_ID;

    /**
     * 实体类后缀
     */
    private String suffix = "Entity";

    /**
     * 乐观锁版本字段
     */
    private String versionColumn = "rev_";

    /**
     * 逻辑删除字段
     */
    private String deletedColumn = "deleted_";

    /**
     * 租户字段，默认没有租户
     */
    private String tenantColumn = StringUtils.EMPTY;

    /**
     * 审计创建时间字段
     */
    private String createTimeColumn = "create_time_";

    /**
     * 审计创建人ID字段
     */
    private String createByColumn = "create_by_";

    /**
     * 审计创建人姓名字段
     */
    private String createNameByColumn = "create_name_by_";

    /**
     * 审计修改时间字段
     */
    private String modifiedTimeColumn = "modified_time_";

    /**
     * 审计修改人ID字段
     */
    private String modifiedByColumn = "modified_by_";

    /**
     * 审计修改人姓名字段
     */
    private String modifiedNameByColumn = "modified_name_by_";
}
