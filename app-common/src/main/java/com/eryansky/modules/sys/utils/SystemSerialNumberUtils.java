/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.lock.DefaultLockCallback;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.service.SystemSerialNumberService;
import com.eryansky.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
public class SystemSerialNumberUtils {

    private static Logger logger = LoggerFactory.getLogger(SystemSerialNumberUtils.class);

    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    public static final class Static {
        private static SystemSerialNumberService systemSerialNumberService = SpringContextHolder.getBean(SystemSerialNumberService.class);

    }

    /**
     * @param id
     * @return
     */
    public static SystemSerialNumber get(String id) {
        if (StringUtils.isNotBlank(id)) {
            return Static.systemSerialNumberService.get(id);
        }
        return null;
    }

    /**
     * @param moduleCode
     * @return
     */
    public static SystemSerialNumber getByModuleCode(String moduleCode) {
        return getByModuleCode(null,moduleCode);
    }

    /**
     * @param app
     * @param moduleCode
     * @return
     */
    public static SystemSerialNumber getByModuleCode(String app,String moduleCode) {
        if (StringUtils.isNotBlank(moduleCode)) {
            return Static.systemSerialNumberService.getByCode(app,moduleCode);
        }
        return null;
    }


    /**
     * 获得当前最大值
     *
     * @param moduleCode
     * @return
     */
    public static String getMaxSerialByModuleCode(String moduleCode) {
        return getMaxSerialByModuleCode(null,moduleCode);
    }

    /**
     * 获得当前最大值
     *
     * @param app
     * @param moduleCode
     * @return
     */
    public static String getMaxSerialByModuleCode(String app,String moduleCode) {
        SystemSerialNumber systemSerialNumber = getByModuleCode(app,moduleCode);
        if (systemSerialNumber != null) {
            return systemSerialNumber.getMaxSerial();
        }
        return null;
    }

    /**
     * 根据模块code生成序列号
     *
     * @param moduleCode 模块code
     * @return 序列号
     */
    public static String generateSerialNumberByModelCode(String moduleCode) {
        return generateSerialNumberByModelCode(null,moduleCode,null,null);
    }

    /**
     * 根据模块code生成序列号
     *
     * @param app APP标识
     * @param moduleCode 模块code
     * @param timeoutInSecond 获取锁超时时间 单位：秒
     * @param keyExpireSeconds 锁超时时间（使用redis有效） 单位：秒
     * @return 序列号
     */
    public static String generateSerialNumberByModelCode(String app,String moduleCode,Integer timeoutInSecond, Long keyExpireSeconds) {
        app = null == app ? SystemSerialNumber.DEFAULT_ID : app;
        String region = SystemSerialNumber.QUEUE_KEY + "_" + app + "_" + moduleCode;
        CacheChannel cacheChannel = CacheUtils.getCacheChannel();
        synchronized (region.intern()) {
            String value = cacheChannel.queuePop(region);
            if (value != null) {
                return value;
            }
            String lockKey = SystemSerialNumber.LOCK_KEY + "_" + app + "_" +  moduleCode;
            String finalApp = app;
            boolean flag = cacheChannel.lock(lockKey, null != timeoutInSecond ? timeoutInSecond : 60, null != keyExpireSeconds ? keyExpireSeconds : 180, new DefaultLockCallback<Boolean>(false, false) {
                @Override
                public Boolean handleObtainLock() {
                    List<String> list = Static.systemSerialNumberService.generatePrepareSerialNumbers(finalApp, moduleCode);
                    for (String serial : list) {
                        cacheChannel.queuePush(region, serial);
                    }
                    return true;
                }
            });
            if (!flag) {
                logger.error("生成序列号失败，锁超时，{}",new Object[]{region});
                return null;
            }
            value = cacheChannel.queuePop(region);
            return value;
        }

    }

}
