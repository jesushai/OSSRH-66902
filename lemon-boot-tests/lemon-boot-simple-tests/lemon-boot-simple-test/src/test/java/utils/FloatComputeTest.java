package utils;

import org.junit.jupiter.api.Test;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-2
 */
public class FloatComputeTest {

    @Test
    public void test1() {
        int w = 1024;
        int h = 960;
        float r = (float) w / 2 - (float) (w - h) / 2;
        System.out.println(r);
    }

    @Test
    public void test2() {
    }
}
