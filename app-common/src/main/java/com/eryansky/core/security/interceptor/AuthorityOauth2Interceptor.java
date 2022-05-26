/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security.interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.net.IpUtils;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.security.ApplicationSessionContext;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.PrepareOauth2;
import com.eryansky.core.security.jwt.JWTUtils;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.List;


/**
 * 模拟Outho2认证拦截器
 * 优先级：注解>数据库权限配置
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2021-09-09
 */
public class AuthorityOauth2Interceptor implements AsyncHandlerInterceptor {


    protected Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 不需要拦截的资源
     */
    private List<String> excludeUrls = Lists.newArrayList();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //已登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (null != sessionInfo && request.getSession().getId().equals(sessionInfo.getId())) {
            return true;
        }

        //注解处理 满足设置不拦截
        HandlerMethod handler = null;
        try {
            handler = (HandlerMethod) o;
            Object bean = handler.getBean();
            PrepareOauth2 prepareOauth2Method = handler.getMethodAnnotation(PrepareOauth2.class);
            PrepareOauth2 prepareOauth2Class = this.getAnnotation(bean.getClass(), PrepareOauth2.class);
            if ((prepareOauth2Method != null && !prepareOauth2Method.enable()) || (prepareOauth2Class != null && !prepareOauth2Class.enable())) {
                return true;
            }
        } catch (ClassCastException e) {
            logger.error(e.getMessage());
        }

        //自动登录
        String authorization = request.getParameter(AuthorityInterceptor.ATTR_AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            authorization = request.getHeader("Authorization");
        }
        if (StringUtils.isNotBlank(authorization)) {
            String requestUrl = request.getRequestURI();
            boolean verify = false;
            String token = StringUtils.replaceOnce(authorization, "Bearer ", "");
            String loginName = null;
            User user = null;
            try {
                loginName = JWTUtils.getUsername(token);
                user = UserUtils.getUserByLoginName(loginName);
                if(null == user){
                    logger.warn("{},Token校验失败（用户不存在）,{},{}", loginName, requestUrl, token);
                    return true;
                }
                verify = JWTUtils.verify(token, loginName, user.getPassword());
            } catch (Exception e) {
                if(!(e instanceof TokenExpiredException)){
                    logger.error("{}-{},Token校验失败,{},{},{}", SpringMVCHolder.getIp(),loginName, requestUrl, token, e.getMessage());
                }
            }
            if (verify && null != user) {
                if(null != sessionInfo){
                    SecurityUtils.addExtendSession(request.getSession().getId(),sessionInfo.getId());
                    logger.debug("{},{},自动跳过登录,{},{},{}", loginName, IpUtils.getIpAddr0(request), requestUrl,request.getSession().getId(),sessionInfo.getId());
                }else{
                    SecurityUtils.putUserToSession(request,user);
                    logger.debug("{},{},自动登录成功,{}", loginName, IpUtils.getIpAddr0(request), requestUrl);
                }

            } else {
//                logger.warn("自动登录失败,{}",authorization);
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if (e != null) {

        }
    }


    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public AuthorityOauth2Interceptor setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
        return this;
    }

    public AuthorityOauth2Interceptor addExcludeUrl(String excludeUrl) {
        this.excludeUrls.add(excludeUrl);
        return this;
    }

    private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        T result = clazz.getAnnotation(annotationType);
        if (result == null) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getAnnotation(superclass, annotationType);
            } else {
                return null;
            }
        } else {
            return result;
        }
    }

}
