package com.site.blog.timer.tasks;

import cn.hutool.core.lang.Console;
import com.site.blog.timer.TimerTaskRunner;
import org.springframework.stereotype.Component;

/**
 * @author yanni
 * @date time 2022/6/17 15:53
 * @modified By:
 */
@Component
public class ConsoleTask implements TimerTaskRunner {
    @Override
    public void action() {
        Console.log("http://localhost:2801/swagger-ui.html");
    }
}
