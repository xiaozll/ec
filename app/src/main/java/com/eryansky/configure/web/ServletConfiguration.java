package com.eryansky.configure.web;

import com.eryansky.core.web.servlet.DownloadChartServlet;
import com.eryansky.core.web.servlet.StaticContentServlet;
import com.eryansky.common.web.servlet.ValidateCodeServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Servlet注册 配置
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2019-01-23
 */
@Configuration
public class ServletConfiguration {

    /**
     * 验证码
     * @return
     */
    @Bean
    public ServletRegistrationBean<ValidateCodeServlet> getValidateCodeServlet() {
        ValidateCodeServlet servlet = new ValidateCodeServlet();
        ServletRegistrationBean<ValidateCodeServlet> bean = new ServletRegistrationBean<>(servlet);
        bean.addUrlMappings("/servlet/ValidateCodeServlet");
        return bean;
    }

    /**
     * 本地静态内容展示与下载的Servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean<StaticContentServlet> getStaticContentServlet() {
        StaticContentServlet servlet = new StaticContentServlet();
        ServletRegistrationBean<StaticContentServlet> bean = new ServletRegistrationBean<>(servlet);
        bean.addUrlMappings("/servlet/StaticContentServlet");
        bean.addInitParameter("cacheChannel","cacheChannel");
        bean.addInitParameter("cacheKey","contentInfoCache");
        bean.addInitParameter("cacheFileData","true");
        return bean;
    }


    /**
     * HightChart download
     * @return
     */
    @Bean
    public ServletRegistrationBean<DownloadChartServlet> getDownloadChartServlet() {
        DownloadChartServlet servlet = new DownloadChartServlet();
        ServletRegistrationBean<DownloadChartServlet> bean = new ServletRegistrationBean<>(servlet);
        bean.addUrlMappings("/servlet/DownloadChartServlet");
        return bean;
    }
}
