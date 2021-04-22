package mp.module;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 名称：自动填充<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/18
 */
@Slf4j
public class MyFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill...");
        Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
        this.fillStrategy(metaObject, "addTime", now);
        this.fillStrategy(metaObject, "updateTime", now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill...");
        Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
        this.fillStrategy(metaObject, "updateTime", now);
    }
}
