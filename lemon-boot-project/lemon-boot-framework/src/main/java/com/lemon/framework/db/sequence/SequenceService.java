package com.lemon.framework.db.sequence;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2019/9/28
 */
@RequestMapping("/sequence")
public interface SequenceService {

    @GetMapping(value = "/generate/{name}", produces = "application/json; charset=utf-8")
    @ResponseBody
    List<Long> generate(@PathVariable("name") String name,
                        @RequestParam(required = false, defaultValue = "1") int count);

    @GetMapping(value = "/generateOne/{name}", produces = "application/json; charset=utf-8")
    @ResponseBody
    default Long generateOne(@PathVariable("name") String name) {
        return generate(name, 1).get(0);
    }
}
