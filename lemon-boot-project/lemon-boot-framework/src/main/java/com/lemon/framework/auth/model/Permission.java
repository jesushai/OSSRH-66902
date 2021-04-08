package com.lemon.framework.auth.model;

import com.lemon.framework.core.annotation.PermissionDescription;
import lombok.Data;

/**
 * <b>名称：权限许可</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@Data
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
