package com.lemon.framework.core.aspect.support;

import com.lemon.framework.core.enums.LimitType;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/19
 */
public interface ApiLimitData {

    String getKey();

    String getName();

    LimitType getLimitType();

    Integer getPeriod();

    Integer getCount();

    Boolean getActive();
}
