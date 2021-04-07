package com.lemon.framework.auth;

import com.lemon.framework.auth.model.Session;
import com.lemon.framework.auth.model.Subject;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.constant.AppContextConstants;
import com.lemon.framework.core.context.AppContextHolder;

/**
 * <b>名称：统一身份接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/15
 */
public interface AuthenticationService {

    /**
     * 当前操作人主题
     */
    default Subject getSubject() {
        Subject subject = (Subject) AppContextHolder.getContext().get(AppContextConstants.SUBJECT);
        if (null == subject) {
            subject = doGetSubject();
            if (null != subject) {
                AppContextHolder.getContext().set(AppContextConstants.SUBJECT, subject);
            }
        }
        return subject;
    }

    /**
     * 当前操作人
     */
    default User getPrincipal() {
        Subject subject = getSubject();
        if (null != subject) {
            return subject.getPrincipal();
        } else {
            return null;
        }
    }

    /**
     * 会话
     */
    default Session getSession() {
        Subject subject = getSubject();
        if (null != subject) {
            return subject.getSession();
        } else {
            return null;
        }
    }

    Subject doGetSubject();

}
