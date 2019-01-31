package com.eryansky;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

//@ComponentScan(value = {"com.eryansky.j2cache.autoconfigure",
//        "com.eryansky.common.spring",
//        "com.eryansky.configure",
//        "com.eryansky.modules.**.task",
//        "com.eryansky.modules.**.service",
//        "com.eryansky.modules.**.quartz",
//        "com.eryansky.modules.**.web"
//})
@EnableTransactionManagement
@SpringBootApplication(exclude={MybatisAutoConfiguration.class, FreeMarkerAutoConfiguration.class, DataSourceAutoConfiguration.class})
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
