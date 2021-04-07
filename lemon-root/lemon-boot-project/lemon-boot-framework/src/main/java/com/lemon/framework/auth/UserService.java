package com.lemon.framework.auth;

import com.lemon.framework.auth.model.User;

import java.util.List;

/**
 * <b>名称：系统用户服务接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
public interface UserService {

    /**
     * 获取指定租户下的指定标识用户<br/>
     * 必须租户内唯一，可以是username，电话，电子邮件，微信号，身份证等等
     */
    List<User> getUserByIdentificationAndTenant(String identification, Long tenantId);

    /**
     * 返回指定用户标识在所有租户下的档案<br/>
     * 用户的唯一标识可以是username，电话，电子邮件，微信号，身份证等等
     */
    List<User> getAllUserByIdentification(String identification);

}
