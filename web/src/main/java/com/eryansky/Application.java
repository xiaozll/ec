package com.eryansky;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

//@EnableScheduling
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
@EnableTransactionManagement
@SpringBootApplication(exclude={MybatisAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }
}
