package com.lemon.framework.auth.model.support;

import com.lemon.framework.auth.model.Session;
import lombok.Data;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Data
public class SessionSupport implements Session {

    private String id;

    private String host;
}
