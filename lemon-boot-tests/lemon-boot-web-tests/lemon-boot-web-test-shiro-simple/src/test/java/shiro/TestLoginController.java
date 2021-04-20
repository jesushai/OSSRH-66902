package shiro;

import com.lemon.framework.util.JacksonUtils;
import com.lemon.framework.util.crypto.password.PasswordEncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/12
 */
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class TestLoginController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SuppressWarnings("unchecked")
    void testLoginByTenant() throws Exception {

        //--------登录
        Map<String, String> params = new LinkedHashMap<String, String>(3) {{
            put("username", "admin");
            put("password", "1234");
            put("tenantId", "0");
        }};
        String content = JacksonUtils.toJson(params);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // 结果转码
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        int status = mvcResult.getResponse().getStatus();
        content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
        Assertions.assertEquals(200, status);
        Assertions.assertEquals("200", JacksonUtils.parseString(content, "code"));

        //--------获取信息
        Map<String, Object> map = JacksonUtils.parseObject(content, "data", HashMap.class);
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/auth/info")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Test-Token", map.get("token"))
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        status = mvcResult.getResponse().getStatus();
        content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
        Assertions.assertEquals(200, status);
    }

    /**
     * 获取用户所在租户的所有权限（树结构）<br/>
     * 支持国际化菜单与功能
     */
    @Test
    void testPermissionTreeByUserToken() throws Exception {

        //--------查询
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/permission/tree")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "zh-CN")
                .header("X-Test-Token", "51950107497394176")
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // 结果转码
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
        Assertions.assertEquals(200, status);
        Assertions.assertEquals("200", JacksonUtils.parseString(content, "code"));
    }

    @Test
    void userPassword() {
        System.out.println(PasswordEncoderUtil.encryptPassword("123"));
    }

}
