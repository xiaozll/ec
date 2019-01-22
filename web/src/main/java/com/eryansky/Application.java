package com.eryansky;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan(value = {"com.eryansky.j2cache.autoconfigure",
        "com.eryansky.common.spring",
        "com.eryansky.configure",
//        "com.eryansky.modules.fop.web",
        "com.eryansky.modules.fop.task",
        "com.eryansky.modules.fop.manager",
        "com.eryansky.modules.fop.service",
        "com.eryansky.modules.sys.service",
        "com.eryansky.modules.sys.web",
        "com.eryansky.modules.disk.service",
        "com.eryansky.modules.disk.web",
        "com.eryansky.modules.notice.service",
        "com.eryansky.modules.notice.web"
})
// mapper 接口类扫描包配置
@MapperScan(value = {"com.eryansky.modules.sys.dao",
        "com.eryansky.modules.disk.dao",
        "com.eryansky.modules.notice.dao"})
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

}
