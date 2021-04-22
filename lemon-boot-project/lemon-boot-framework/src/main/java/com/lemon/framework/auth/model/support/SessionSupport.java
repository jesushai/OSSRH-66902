package com.lemon.framework.auth.model.support;

import com.lemon.framework.auth.model.Session;
import lombok.Data;

/**
 * 名称：Session<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Data
public class SessionSupport implements Session {

    /**
     * Session id
     */
    private String id;

    /**
     * Session host
     */
    private String host;
}
