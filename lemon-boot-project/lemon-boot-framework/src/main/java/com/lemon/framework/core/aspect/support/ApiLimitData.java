package com.lemon.framework.core.aspect.support;

import com.lemon.framework.core.enums.LimitType;

/**
 * 名称：<p>
 * 描述：<p>
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
