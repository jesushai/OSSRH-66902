package com.lemon.framework.auth.model;

import com.lemon.framework.core.annotation.PermissionDescription;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 名称：权限许可<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@Data
@Accessors(chain = true)
public class Permission {

    /**
     * 权限许可ID<br/>
     * 如果是shiro则对应着RequiresPermissions.value
     */
    private String[] id;

    /**
     * 权限许可描述
     */
    private PermissionDescription description;

    /**
     * 对应的API信息
     */
    private String api;

}
