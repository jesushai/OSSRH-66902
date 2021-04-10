package mp.module.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import mp.module.entity.Brand;

/**
 * <p>
 * 品牌商表 服务类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-04-18
 */
public interface IBrandService extends IService<Brand> {

    IPage<Brand> querySelective(String id, String name, IPage<Brand> page);
}
