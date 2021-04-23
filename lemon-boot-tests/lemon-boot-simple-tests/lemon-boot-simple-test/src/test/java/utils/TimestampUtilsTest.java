package utils;

import com.lemon.framework.util.TimestampUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 名称：<p>
 * 描述：<p>
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
