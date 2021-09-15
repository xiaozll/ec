/**
 *  Copyright (c) 2012-2020 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security.interceptor;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.jwt.JWTUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        //登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(null != sessionInfo){
            return true;
        }
        //自动登录
        String authorization = request.getParameter(AuthorityInterceptor.ATTR_AUTHORIZATION);
        if(StringUtils.isNotBlank(authorization)){
            boolean verify = false;
            String token = StringUtils.replaceOnce(authorization, "Bearer ", "");
            String loginName = null;
            try {
                loginName = JWTUtils.getUsername(token);
                verify = JWTUtils.verify(token,loginName,loginName);
            } catch (Exception e) {
                logger.error("{},{},Token校验失败,{}", token,loginName,e.getMessage());
            }
            if(verify){
                SecurityUtils.putUserToSession(request, UserUtils.getUserByLoginName(loginName));
                logger.warn("header|Authorization:{},自动登录成功",loginName);
            }else{
                logger.warn("header|Authorization:{},自动登录失败",authorization);
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if(e != null){

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

}
