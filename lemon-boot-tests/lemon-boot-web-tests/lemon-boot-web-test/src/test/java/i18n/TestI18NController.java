package i18n;

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
import org.springframework.util.LinkedMultiValueMap;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 * 这两个测试只有一个能通过，决定在于application.yml中的配置zh.http.accept-language
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TestI18NController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRestLocale() throws Exception {
        String url = "/test/hello";

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "zh");
        params.add("age", "100");
        params.add("lang", "zh_CN");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .params(params)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // 结果转码
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(200, status);
        Assertions.assertTrue(content.contains("这是一条消息"));

        params = new LinkedMultiValueMap<>();
        params.add("name", "zh");
        params.add("age", "100");
        params.add("lang", "en");
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .params(params)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // 结果转码
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        status = mvcResult.getResponse().getStatus();
        content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(200, status);
        Assertions.assertTrue(content.contains("This is a message"));
    }

    @Test
    public void testRestLocaleByHeader() throws Exception {
        String url = "/test/hello";

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "zh");
        params.add("age", "100");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .params(params)
                .header("Accept-Language", "zh-CN")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // 结果转码
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(200, status);
        Assertions.assertTrue(content.contains("这是一条消息"));

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .params(params)
                .header("Accept-Language", "en")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        status = mvcResult.getResponse().getStatus();
        content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(200, status);
        Assertions.assertTrue(content.contains("This is a message"));
    }

}
