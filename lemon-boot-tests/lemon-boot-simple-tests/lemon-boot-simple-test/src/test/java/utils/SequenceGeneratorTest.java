package utils;

import com.lemon.framework.util.sequence.SequenceGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2021/3/3
 */
public class SequenceGeneratorTest {

    @Test
    public void test30000() {
        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        int cnt = 100000;

        long[] ids = new long[cnt];
        for (int i = 0; i < cnt; i++) {
            ids[i] = sequenceGenerator.nextId();
        }

        Set<Long> s = new HashSet<>();
        for (int i = 0; i < cnt; i++) {
            if (s.contains(ids[i])) {
                System.out.println(ids[i]);
            } else {
                s.add(ids[i]);
            }
        }
    }
}
