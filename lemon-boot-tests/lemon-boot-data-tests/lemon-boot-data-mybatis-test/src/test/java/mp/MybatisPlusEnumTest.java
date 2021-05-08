package mp;

import com.lemon.framework.util.JacksonUtils;
import mp.enums.RegionTypeEnum;
import mp.module.entity.Region;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021-4-10
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MybatisPlusEnumTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNewRegion() throws Exception {
        String url = "/region";

//        List<Region> regions = new ArrayList<Region>(3) {{
//            add(new Region().setCode(110000).setName("辽宁").setType(RegionTypeEnum.Province));
//            add(new Region().setCode(120000).setName("吉林").setType(RegionTypeEnum.Province));
//            add(new Region().setCode(130000).setName("黑龙江").setType(RegionTypeEnum.Province));
//        }};

        List<Region> regions = new ArrayList<Region>(3) {{
            add(new Region().setPid(168623965718593536L).setCode(1324123).setName("盘锦").setType(RegionTypeEnum.City));
            add(new Region().setPid(168623965718593536L).setCode(41324).setName("沈阳").setType(RegionTypeEnum.City));
            add(new Region().setPid(168623965718593536L).setCode(21343214).setName("大连").setType(RegionTypeEnum.City));
        }};

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON);

        for (Region region : regions) {
            MvcResult mvcResult = mockMvc.perform(
                    requestBuilder.content(JacksonUtils.parseString(region))
            )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();

            // 结果转码
            mvcResult.getResponse().setCharacterEncoding("UTF-8");
            int status = mvcResult.getResponse().getStatus();
            String content = mvcResult.getResponse().getContentAsString();
            Assertions.assertEquals(200, status);
            System.out.println(content);
        }
    }

    @Test
    public void testGetRegionById() throws Exception {
        String url = "/region";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "168623965718593536")
        )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        // 结果转码
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(200, status);
        System.out.println(content);
    }

    @Test
    public void testGetAllProvince() throws Exception {
        String url = "/region/provinces";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
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
        System.out.println(content);
    }

}
