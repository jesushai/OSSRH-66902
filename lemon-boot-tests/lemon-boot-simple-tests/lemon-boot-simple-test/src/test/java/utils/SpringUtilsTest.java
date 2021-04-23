package utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/18
 */
class SpringUtilsTest {

    @Test
    void testSplitter() {
        String s = "11,22,33,";
        System.out.println(Arrays.toString(StringUtils.split(s, ',')));
    }

}
