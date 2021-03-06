package serilog.entity;

import com.lemon.framework.auth.model.User;
import lombok.Data;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-14
 */
@Data
public class SysUserEntity implements User {

    private Long id;

    private Long tenant;

    private String username;

    private String password;

    private Long[] roleIds;

    private boolean valid;
}
