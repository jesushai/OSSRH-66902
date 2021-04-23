package com.lemon.framework.db.sequence;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
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
