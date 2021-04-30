package com.lemon.framework.util.test;

import com.lemon.framework.util.sequence.SequenceGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/3/3
 */
class SequenceGeneratorTest {

    @Test
    void test30000() {
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
