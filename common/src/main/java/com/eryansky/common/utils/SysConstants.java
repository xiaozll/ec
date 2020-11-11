/**
 *  Copyright (c) 2012-2020 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils;

import com.eryansky.common.spring.SpringContextHolder;
import org.springframework.core.env.StandardEnvironment;


/**
 * 项目中用到的静态变量.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-20 上午11:40:56
 */
public class SysConstants {
    /**
     * session 验证码key
     */
    public static final String SESSION_VALIDATE_CODE = "validateCode";

    private static class SysConstantsHolder {
        private static final StandardEnvironment standardEnvironment = SpringContextHolder.getBean(StandardEnvironment.class);
    }

    /**
     * 配置文件(appconfig.properties)
     */
    public static StandardEnvironment getAppConfig() {
        return SysConstantsHolder.standardEnvironment;
    }

    /**
     * 获取配置
     */
    public static String getAppConfig(String key) {
        return SysConstants.getAppConfig().getProperty(key);
    }

    /**
     * 获取配置
     */
    public static String getAppConfig(String key,String defaultValue) {
        return SysConstants.getAppConfig().getProperty(key,defaultValue);
    }

    /**
     * jdbc type连接参数(默认:"").
     */
    public static String getJdbcType(){
        return SysConstants.getAppConfig().getProperty("jdbc.type","");
    }
    
    /**
     * jdbc url连接参数(默认:"").
     */
    public static String getJdbcUrl(){
    	return SysConstants.getAppConfig().getProperty("spring.datasource.url","");
    }

    /**
     * jdbc 驱动类
     * @return
     */
    public static String getJdbcDriverClassName(){
        return SysConstants.getAppConfig().getProperty("spring.datasource.driver-class-name","");
    }

    /**
     * jdbc 用户名
     * @return
     */
    public static String getJdbcUserName(){
        return SysConstants.getAppConfig().getProperty("spring.datasource.username","");
    }

    /**
     * jdbc 密码
     * @return
     */
    public static String getJdbcPassword(){
        return SysConstants.getAppConfig().getProperty("spring.datasource.password","");
    }


    /**
     * 获取是否是开发模式(默认:false).
     */
    public static Boolean isdevMode(){
    	return Boolean.valueOf(getAppConfig().getProperty("devMode","false"));
    }

}
