/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security.interceptor;

import com.eryansky.common.model.Result;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.net.IpUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.annotation.RestApi;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.CacheUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;


/**
 * Rest权限拦截器
 * @author wencp
 * @date 2020-09-09
 */
public class RestDefaultAuthorityInterceptor implements AsyncHandlerInterceptor {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String PARAM_APP_CODE = "appCode";
    private static final String PARAM_APP_KEY = "appKey";


    /**
     * 不需要拦截的资源
     */
    private List<String> excludeUrls = Lists.newArrayList();
    /**
     * 验证数据库标记URL 默认值：false
     */
    private Boolean authorMarkUrl = false;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestUrl = request.getRequestURI();
        requestUrl = requestUrl.replaceAll("//", "/");
        if (logger.isDebugEnabled()) {
            logger.debug("{} {} {}",request.getSession().getId(),request.getHeader("Authorization"),requestUrl);
        }
        boolean restEnable = AppConstants.getIsSystemRestEnable();
        if (!restEnable) {
            Result result = Result.errorApiResult().setMsg("系统维护中，请稍后再试！");
            logger.warn("{} {} {}",IpUtils.getIpAddr0(request) ,result,requestUrl);
            WebUtils.renderJson(response, result);
            return false;
        }
        String sessionId = request.getSession().getId();
        String cacheKey = sessionId;
//        String cacheKey = sessionId+requestUrl;
        Boolean handlerResult = CacheUtils.get(cacheKey);
        if (null != handlerResult) {
            return handlerResult;
        }
        //注解处理
        handlerResult = this.defaultandler(request, response, o, requestUrl);
        CacheUtils.put(cacheKey, handlerResult);
        if (null != handlerResult) {
            return handlerResult;
        }
        return true;
    }


    /**
     * 注解处理
     * @param request
     * @param response
     * @param o
     * @param requestUrl
     * @return
     * @throws Exception
     */
    private Boolean defaultandler(HttpServletRequest request, HttpServletResponse response, Object o, String requestUrl) throws Exception {
        HandlerMethod handler = null;
        try {
            handler = (HandlerMethod) o;
        } catch (ClassCastException e) {
//                logger.error(e.getMessage(),e);
        }

        if (handler != null) {
            Object bean = handler.getBean();
            //权限校验
            RestApi restApi = handler.getMethodAnnotation(RestApi.class);
            if (restApi == null) {
                restApi = this.getAnnotation(bean.getClass(), RestApi.class);
            }
            if (restApi != null) {//方法注解处理
                if (!restApi.required()) {
                    return true;
                }
                String appCode = WebUtils.getParameter(request, PARAM_APP_CODE);
                String appKey = WebUtils.getParameter(request, PARAM_APP_KEY);
                String apiKey = request.getHeader("X-API-Key");
                if (StringUtils.isBlank(appCode) && StringUtils.isBlank(appKey) && StringUtils.isBlank(apiKey)) {
                    notPermittedPermission(request, response, requestUrl, "未识别appCode、appKey或apiKey参数:" + appCode);
                    return false;
                }
                //密钥认证
                String DEFAULT_API_KEY = AppConstants.getRestDefaultApiKey();
                if (!DEFAULT_API_KEY.equals(appKey) || !DEFAULT_API_KEY.equals(apiKey)) {
                    notPermittedPermission(request, response, requestUrl, "未授权访问:" + appCode);
                    return false;
                }

                //IP访问限制
                boolean isRestLimitEnable = AppConstants.getIsSystemRestLimitEnable();
                if (isRestLimitEnable) {
                    String ip = IpUtils.getIpAddr0(request);
                    if("127.0.0.1".equals(ip) || "localhost".equals(ip)){
                        return true;
                    }
                    //TODO 配置IP白名单
                    List<String> ipList = AppConstants.getRestLimitIpWhiteList();
                    if (null == ipList.stream().filter(v -> "*".equals(v) || com.eryansky.j2cache.util.IpUtils.checkIPMatching(v, ip)).findAny().orElse(null)) {
                        notPermittedPermission(request, response, requestUrl, "REST禁止访问：" + ip);
                        return false;
                    }
                }
                return true;
            }

        }
        return null;
    }

    /**
     * 未授权权限
     * @param request
     * @param response
     * @param requestUrl
     * @throws ServletException
     * @throws IOException
     */
    private void notPermittedPermission(HttpServletRequest request, HttpServletResponse response, String requestUrl, String msg) throws ServletException, IOException {
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Result result = Result.noPermissionResult().setMsg(msg);
        logger.warn(IpUtils.getIpAddr0(request) + " " + result.toString() + " " + requestUrl);
        WebUtils.renderJson(response, result);
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

    public RestDefaultAuthorityInterceptor setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
        return this;
    }

    public RestDefaultAuthorityInterceptor addExcludeUrl(String excludeUrl) {
        this.excludeUrls.add(excludeUrl);
        return this;
    }

    public Boolean getAuthorMarkUrl() {
        return authorMarkUrl;
    }

    public RestDefaultAuthorityInterceptor setAuthorMarkUrl(Boolean authorMarkUrl) {
        this.authorMarkUrl = authorMarkUrl;
        return this;
    }
}
