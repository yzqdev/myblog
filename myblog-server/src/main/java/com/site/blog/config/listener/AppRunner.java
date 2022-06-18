package com.site.blog.config.listener;

import cn.hutool.core.lang.Console;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yanni
 * @date time 2022/6/15 17:32
 * @modified By:
 */
@Order(2)
@Component
public class AppRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Console.log("http://localhost:2800");
    }
}
