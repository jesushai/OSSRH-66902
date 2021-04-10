package mp.module.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mp.module.entity.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 品牌商表 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2020-04-18
 */
public interface BrandMapper extends BaseMapper<Brand> {

    List<Brand> getAllByName(@Param("name") String name);

}
