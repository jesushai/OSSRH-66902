package com.lemon.framework.core.aspect.support;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 名称：Api限流仓库类<br/>
 * 描述：<br/>
 * 可以继承这个组件，并从DB中获取API限流配置
 *
 * @author hai-zhang
 * @since 2020/6/19
 */
@Component
@SuppressWarnings("unchecked")
public class ApiLimitRepositorySupport {

    public List<ApiLimitData> getAllApiLimit() {
        return Collections.EMPTY_LIST;
    }

    public ApiLimitData getApiLimit(String key) {
        return null;
    }
}
