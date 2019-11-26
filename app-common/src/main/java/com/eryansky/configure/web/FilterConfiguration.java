package com.eryansky.configure.web;

import com.eryansky.common.web.filter.CustomHttpServletRequestFilter;
import com.eryansky.core.web.filter.MySiteMeshFilter;
import com.eryansky.core.web.interceptor.ExceptionInterceptor;
import com.eryansky.filters.ChinesePathFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;


/**
 * Filter注册配置
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2019-01-23
 */
@Configuration
public class FilterConfiguration {

    /**
     * 中文
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<ChinesePathFilter> chinesePathFilterFilterRegistrationBean() {
        ChinesePathFilter filter = new ChinesePathFilter();
        FilterRegistrationBean<ChinesePathFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setFilter(filter);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE+100);
        return bean;
    }


    /**
     * SiteMesh自定义Filter
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<MySiteMeshFilter> mySiteMeshFilterFilterRegistrationBean() {
        MySiteMeshFilter filter = new MySiteMeshFilter();
        FilterRegistrationBean<MySiteMeshFilter> bean = new FilterRegistrationBean<>(filter);
        bean.addInitParameter("blackListURL", "/static/**");
        bean.addInitParameter("whiteListURL", "/**");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 300);
        return bean;
    }


    /**
     * 自定义URL拦截
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<CustomHttpServletRequestFilter> customHttpServletRequestFilterFilterRegistrationBean() {
        CustomHttpServletRequestFilter filter = new CustomHttpServletRequestFilter();
        FilterRegistrationBean<CustomHttpServletRequestFilter> bean = new FilterRegistrationBean<>(filter);
        bean.addInitParameter("blackListURL", "/static/**");
        bean.addInitParameter("whiteListURL", "/a/sys/proxy/**");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 200);
        return bean;
    }


    /**
     * 自定义异常处理
     *
     * @return
     */
    @Bean
    public ExceptionInterceptor exceptionInterceptor() {
        ExceptionInterceptor bean = new ExceptionInterceptor();
        return bean;
    }

}
