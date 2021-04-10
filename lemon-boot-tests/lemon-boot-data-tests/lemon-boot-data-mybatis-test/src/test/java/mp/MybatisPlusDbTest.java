package mp;

import mp.module.entity.Brand;
import mp.module.service.IBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@SpringBootTest
//@RunWith(SpringRunner.class)
public class MybatisPlusDbTest {

    //region 测试查询商品多条件带分页排序，查询会自动携带Blob字段
    @Resource
    private IBrandService brandService;

    @Test
    public void testServiceQuery() {
        List<Brand> brands = brandService.lambdaQuery().gt(Brand::getName, "").list();
        System.out.println(brands);
    }

    @Test
    public void testNewBrand() {
        Brand brand = new Brand();
        brand.setName("102394");
        brand.setDesc("1890234");
        brandService.save(brand);
    }
    //endregion

}
