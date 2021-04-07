package com.lemon.framework.core.enums;

public enum LimitType {
    /**
     * 传统类型：key+ip限制
     */
    CUSTOMER,
    /**
     * 仅根据 IP地址限制
     */
    IP,
    /**
     * 根据user+key限制
     */
    PRINCIPAL_KEY,
    /**
     * 根据user限制
     */
    PRINCIPAL
}
