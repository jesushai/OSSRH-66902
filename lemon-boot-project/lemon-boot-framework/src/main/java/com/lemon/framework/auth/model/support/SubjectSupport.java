package com.lemon.framework.auth.model.support;

import com.lemon.framework.auth.model.Session;
import com.lemon.framework.auth.model.Subject;
import com.lemon.framework.auth.model.User;
import lombok.Data;

/**
 * 名称：Subject<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Data
public class SubjectSupport implements Subject {

    /**
     * Session
     */
    private Session session;

    /**
     * 主体
     */
    private User principal;
}
