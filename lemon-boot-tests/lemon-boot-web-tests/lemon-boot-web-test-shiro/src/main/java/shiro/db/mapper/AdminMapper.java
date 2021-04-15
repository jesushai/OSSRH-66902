package shiro.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import shiro.db.entity.Admin;

/**
 * <p>
 * 系统人员 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2020-06-12
 */
public interface AdminMapper extends BaseMapper<Admin> {

    IPage<Admin> selectPageByExample(IPage<Admin> page, @Param("example") Admin example);
}
