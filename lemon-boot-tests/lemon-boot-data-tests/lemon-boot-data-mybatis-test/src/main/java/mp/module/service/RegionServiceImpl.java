package mp.module.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mp.enums.RegionTypeEnum;
import mp.module.entity.Region;
import mp.module.mapper.RegionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021-4-10
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements IRegionService {

    @Override
    public List<Region> getAllProvince() {
        return baseMapper.selectList(new QueryWrapper<Region>().lambda().eq(Region::getType, RegionTypeEnum.Province));
    }

}
