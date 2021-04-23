package com.lemon.schemaql.config.aspect;

import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：动态切片配置<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@EqualsAndHashCode(of = {"aspectName", "operationTypes"})
public class DynamicAspectConfig {

    public DynamicAspectConfig() {
    }

    public DynamicAspectConfig(String aspectName, String comment, String packageName, OperationTypeEnum[] operationTypes, int ordered) {
        this.aspectName = aspectName;
        this.comment = comment;
        this.packageName = packageName;
        this.operationTypes = operationTypes;
        this.ordered = ordered;
    }

    /**
     * 切片类名
     */
    private String aspectName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 操作类型
     */
    private OperationTypeEnum[] operationTypes;

    /**
     * 执行顺序
     */
    private int ordered;

}
