package shiro.db.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * <b>名称：自动填充</b><br/>
 * <b>描述：</b><br/>
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
