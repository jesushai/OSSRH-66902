package redisson;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

//import org.redisson.RedissonScript;
//import org.redisson.api.RScript;
//import org.redisson.api.RedissonClient;
//import org.redisson.codec.JsonJacksonCodec;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/6
 */
@SpringBootTest
@Slf4j
public class TestRedisson {
/*
    @Autowired
    private RedissonClient redisson;

    @Test
    public void testScript() {
        final RedissonScript script = (RedissonScript) redisson.getScript(new JsonJacksonCodec());
        String key = "testKey1";
        List<Object> keys = new ArrayList<>(1);
        keys.add(key);

        long timeout = 10000000L;
        Date startTimeStamp = new Date();
        Date expireTimestamp = new Date(startTimeStamp.getTime() + timeout);

        // 写入redis
        script.eval(key,
                RScript.Mode.READ_WRITE, INIT_SCRIPT, RScript.ReturnType.VALUE,
                keys, "session-id1", timeout, startTimeStamp, "host", expireTimestamp);

        // 读取开始时间
        Date res = script.eval(key, RScript.Mode.READ_ONLY, GET_START_SCRIPT, RScript.ReturnType.MAPVALUE, keys);
        Assert.assertEquals(res, startTimeStamp);

        // 读取所有值
        List<Object> list = script.eval(key, RScript.Mode.READ_ONLY, READ_INFO_SCRIPT, RScript.ReturnType.MULTI, keys);
        System.out.println(list);

    }*/
}
