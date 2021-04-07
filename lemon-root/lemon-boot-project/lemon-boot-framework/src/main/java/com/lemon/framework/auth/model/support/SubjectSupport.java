package com.lemon.framework.auth.model.support;

import com.lemon.framework.auth.model.Session;
import com.lemon.framework.auth.model.Subject;
import com.lemon.framework.auth.model.User;
import lombok.Data;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Data
public class SubjectSupport implements Subject {

    private Session session;

    private User principal;
}
