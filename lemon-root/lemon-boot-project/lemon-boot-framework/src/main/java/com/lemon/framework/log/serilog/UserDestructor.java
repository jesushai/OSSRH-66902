package com.lemon.framework.log.serilog;

import com.lemon.framework.auth.model.User;
import serilogj.core.DestructuringPolicyResult;
import serilogj.core.IDestructuringPolicy;
import serilogj.core.ILogEventPropertyValueFactory;
import serilogj.events.StructureValue;

/**
 * <b>名称：Seq Log 系统用户的属性解析</b><br/>
 * <b>描述：</b><br/>
 * 固定属性标签是 Username
 *
 * @author hai-zhang
 * @since 2020/5/14
 */
public class UserDestructor implements IDestructuringPolicy {

    private final static String TYPE_TAG = "Username";

    @Override
    public DestructuringPolicyResult tryDestructure(Object value, ILogEventPropertyValueFactory propertyValueFactory) {
        DestructuringPolicyResult result = new DestructuringPolicyResult();
        if (!(value instanceof User)) {
            return result;
        } else {
            result.isValid = true;
            User user = (User) value;
            result.result = propertyValueFactory.createPropertyValue(
                    user.getUsername(), true);
            ((StructureValue) result.result).setTypeTag(TYPE_TAG);
            return result;
        }
    }

}
