package utils;

import com.lemon.framework.util.TimestampUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
public class TimestampUtilsTest {

    @Test
    public void testGetOffsetDate() {
        Timestamp now = TimestampUtils.date(2020, 0, 1);
        Timestamp t = TimestampUtils.getOffsetDate(now, 1, Calendar.MONTH);
        Assertions.assertEquals(t, TimestampUtils.date(2020, 1, 1));
    }
}
