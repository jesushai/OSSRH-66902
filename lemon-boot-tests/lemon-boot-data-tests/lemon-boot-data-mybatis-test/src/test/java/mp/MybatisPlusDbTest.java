package mp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import mp.module.entity.Brand;
import mp.module.service.IBrandService;
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

import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MybatisPlusDbTest {

    //region 测试查询商品多条件带分页排序，查询会自动携带Blob字段
    @Autowired
    private IBrandService brandService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testServiceQuery() {
        List<Brand> brands = brandService.lambdaQuery()
                .gt(Brand::getName, "")
                .list();
        System.out.println(brands);
    }

    @Test
    public void testServiceQueryPage() {
        IPage<Brand> brands = brandService.querySelective(null, "94", new Page<>(1, 10));
        System.out.println(brands);
        brands = brandService.querySelective(null, "94", new Page<>(1, 5));
        System.out.println(brands);
    }

    @Test
    public void testNewBrand() {
        Brand brand = new Brand();
        brand.setName("fasfsdf");
        brand.setDesc("fdasdfdsfdsf");
        brandService.save(brand);
    }
    //endregion

    @Test
    public void testDynamicDatasource() throws Exception {
        String url = "/brand/name";

        // TODO: 用户需要登录到租户中

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "C")
                .header("tenant", "100")
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
