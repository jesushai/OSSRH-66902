package com.lemon.schemaql.config;

import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = {"operationTypes", "permissions"})
public class PermissionsConfig {

    /**
     * 授权类型
     */
    private Set<OperationTypeEnum> operationTypes;

    /**
     * 授权
     */
    private Set<String> permissions;

    /**
     * 菜单，支持国际化
     */
    private Set<String> menus;

    /**
     * 按钮，支持国际化
     */
    private String action;
}
