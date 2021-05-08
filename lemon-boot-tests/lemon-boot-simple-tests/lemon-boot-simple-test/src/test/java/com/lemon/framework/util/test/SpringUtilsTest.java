package com.lemon.framework.util.test;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

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
        System.out.println(Arrays.toString(StringUtils.splitByWholeSeparatorPreserveAllTokens(
                " desc:   ", ":", 2)));
        System.out.println(
                Arrays.toString(
                        Splitter.on(':')
                                .limit(2)
                                .trimResults()
                                .splitToList(" @desc")
                                .toArray()
                )
        );

        System.out.println(
                Arrays.toString(
                        Splitter.on(':')
                                .limit(2)
                                .trimResults()
                                .splitToList(" @desc:我的名字:{0}")
                                .toArray()
                )
        );
    }

}
