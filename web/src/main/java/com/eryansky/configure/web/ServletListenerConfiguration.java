package com.eryansky.configure.web;

import com.eryansky.listener.SystemInitListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.util.IntrospectorCleanupListener;


/**
 * Servlet注册 配置
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2019-01-23
 */
@Configuration
public class ServletListenerConfiguration {


    @Bean
    public ServletListenerRegistrationBean<IntrospectorCleanupListener> getIntrospectorCleanupListener() {
        ServletListenerRegistrationBean<IntrospectorCleanupListener> bean = new ServletListenerRegistrationBean<>(new IntrospectorCleanupListener());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<RequestContextListener> getRequestContextListener() {
        ServletListenerRegistrationBean<RequestContextListener> bean = new ServletListenerRegistrationBean<>(new RequestContextListener());
        return bean;
    }

    /**
     * 系统启动、关闭监听
     * @return
     */
    @DependsOn(value = {"springContextHolder"})
    @Bean
    public ServletListenerRegistrationBean<SystemInitListener> getSystemInitListener() {
        ServletListenerRegistrationBean<SystemInitListener> bean = new ServletListenerRegistrationBean<>(new SystemInitListener());
        return bean;
    }

}
