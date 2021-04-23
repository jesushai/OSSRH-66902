package mp.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import mp.module.entity.Region;

import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021-4-10
 */
public interface IRegionService extends IService<Region> {

    List<Region> getAllProvince();
}
