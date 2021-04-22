package com.lemon.framework.auth;

import com.lemon.framework.auth.model.User;

import java.util.List;

/**
 * 名称：系统用户服务接口<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
public interface UserService {

    /**
     * 获取指定租户下的指定标识用户<br/>
     * 必须租户内唯一，可以是username，电话，电子邮件，微信号，身份证等等
     *
     * @param identification 用户唯一身份标识
     * @param tenantId       租户
     * @return 租户内指定标识的用户
     */
    List<User> getUserByIdentificationAndTenant(String identification, Long tenantId);

    /**
     * 返回指定用户标识在所有租户下的档案<br/>
     * 用户的唯一标识可以是username，电话，电子邮件，微信号，身份证等等
     *
     * @param identification 用户唯一身份标识
     * @return 指定标识的用户
     */
    List<User> getAllUserByIdentification(String identification);

}
