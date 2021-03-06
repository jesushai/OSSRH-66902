package mp.web;

import mp.module.entity.Region;
import mp.module.service.IRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021-4-10
 */
@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private IRegionService regionService;

    @GetMapping
    public Object getRegionById(@RequestParam("id") Long id) {
        return regionService.getById(id);
    }

    @GetMapping("/provinces")
    public Object getAllProvince() {
        return regionService.getAllProvince();
    }

    @PostMapping
    public Region newRegion(@RequestBody Region region) {
        regionService.save(region);
        return region;
    }
}
