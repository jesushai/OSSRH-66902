package serilog.entity;

import com.lemon.framework.auth.model.User;
import lombok.Data;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
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
