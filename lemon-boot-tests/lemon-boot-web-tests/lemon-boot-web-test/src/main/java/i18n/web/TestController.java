package i18n.web;

import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.protocal.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@RestController
@RequestMapping("/test")
@ResponseBody
public class TestController {

//    @Resource
//    private MessageSourceHandler messageSourceHandler;

    @GetMapping(value = "/hello", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String hello(@RequestParam("name") String name, @RequestParam("age") Integer age) {
        return MessageSourceHandler.getMessage("TEST-001", name, age);
    }

    @GetMapping(value = "/hello2", produces = "application/json; charset=utf-8")
    @ResponseBody
    public Result helloObject(@RequestParam("name") String name, @RequestParam("age") Integer age) {
        String msg = MessageSourceHandler.getMessage("TEST-001", name, age);
        return Result.ok().data(msg);
    }

}
