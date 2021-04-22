package mp.web;

import com.lemon.framework.db.mp.page.MyPageRequest;
import mp.module.entity.Brand;
import mp.module.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2021-4-12
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private IBrandService brandService;

    @GetMapping("/name")
    public Object findByNameList(@RequestParam(name = "name") String name) {

        List<Brand> result = new ArrayList<>(brandService.querySelective(null, name, MyPageRequest.of(1, 10)).getRecords());
        result.addAll(brandService.querySelectiveSlave(null, name, MyPageRequest.of(1, 10)).getRecords());
        return result;
    }
}
