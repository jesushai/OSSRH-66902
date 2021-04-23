package com.lemon.framework.log.serilog;

import serilogj.core.DestructuringPolicyResult;
import serilogj.core.IDestructuringPolicy;
import serilogj.core.ILogEventPropertyValueFactory;
import serilogj.events.StructureValue;

/**
 * 名称：Seq Log 租户的解析属性<p>
 * 描述：<p>
 * 固定属性标签是TenantId
 *
 * @author hai-zhang
 * @since 2020/5/14
 */
public class TenantDestructor implements IDestructuringPolicy {

    private final static String TYPE_TAG = "TenantId";

    @Override
    public DestructuringPolicyResult tryDestructure(Object value, ILogEventPropertyValueFactory propertyValueFactory) {
        DestructuringPolicyResult result = new DestructuringPolicyResult();
        if (!(value instanceof Long)) {
            return result;
        } else {
            result.isValid = true;
            result.result = propertyValueFactory.createPropertyValue(value, true);
            ((StructureValue) result.result).setTypeTag(TYPE_TAG);
            return result;
        }
    }

}
