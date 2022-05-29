package com.eryansky.core.aop.interceptor;

import com.eryansky.common.model.Result;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.utils.net.IpUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * IP限制请求拦截器
 **/
public class IpLimitInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(IpLimitInterceptor.class);

    public static final String LOCK_IP_LIMIT_REGION = "lock_ip_limit";
    public static final String LOCK_IP_LIMIT_WHITELIST_REGION = "lock_ip_limit_whitelist";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ip = IpUtils.getIpAddr(httpServletRequest);
        log.debug("request请求地址uri={},ip={}", httpServletRequest.getRequestURI(), ip);
        if (ipIsLock(ip)) {
            log.warn("ip访问被禁止={}", ip);
            Result result = Result.errorResult().setMsg("IP访问被禁止=" + ip);
            WebUtils.renderJson(httpServletResponse, JsonMapper.toJsonString(result));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * 判断ip是否被禁用
     *
     * @param ip
     */
    private Boolean ipIsLock(String ip) {
        //白名单
        Collection<String> whiteList = CacheUtils.keys(LOCK_IP_LIMIT_WHITELIST_REGION);
        if (null != whiteList && whiteList.contains(ip)) {
            return false;
        }
        Boolean value = CacheUtils.get(LOCK_IP_LIMIT_REGION, ip);
        return null != value;
    }


}