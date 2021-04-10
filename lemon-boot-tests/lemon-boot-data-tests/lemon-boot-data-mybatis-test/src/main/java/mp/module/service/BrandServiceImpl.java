package mp.module.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mp.module.entity.Brand;
import mp.module.mapper.BrandMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 品牌商表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-04-18
 */
@Service
@DS("#header.tenant + '-master'")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {

    @Override
    public IPage<Brand> querySelective(String id, String name, IPage<Brand> page) {
        LambdaQueryWrapper<Brand> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasLength(id)) {
            queryWrapper.eq(Brand::getId, Long.valueOf(id));
        }
        if (StringUtils.hasLength(name)) {
            queryWrapper.like(Brand::getName, name);
        }

        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @DS("#header.tenant + '-slave'")
    public IPage<Brand> querySelectiveSlave(String id, String name, IPage<Brand> page) {
        return querySelective(id, name, page);
    }

}
