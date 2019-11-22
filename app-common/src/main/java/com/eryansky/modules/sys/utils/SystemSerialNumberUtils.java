/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.j2cache.lock.DefaultLockCallback;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.service.SystemSerialNumberService;
import com.eryansky.utils.CacheUtils;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
public class SystemSerialNumberUtils {


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
        return getByModuleCode(SystemSerialNumber.DEFAULT_ID,moduleCode);
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
        return getMaxSerialByModuleCode(SystemSerialNumber.DEFAULT_ID,moduleCode);
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
        return generateSerialNumberByModelCode(SystemSerialNumber.DEFAULT_ID,moduleCode);
    }

    /**
     * 根据模块code生成序列号
     *
     * @param app APP标识
     * @param moduleCode 模块code
     * @return 序列号
     */
    public static String generateSerialNumberByModelCode(String app,String moduleCode) {
        app = null == app ? SystemSerialNumber.DEFAULT_ID:app;
        String region = SystemSerialNumber.QUEUE_SYS_SERIAL +"_" + app + ":" + moduleCode;
        String value = CacheUtils.getCacheChannel().queuePop(region);
        if (value != null) {
            return value;
        }
        String lockKey = "SystemSerialNumber_lock_"+app+"_"+":" + moduleCode;
        String finalApp = app;
        boolean flag = CacheUtils.getCacheChannel().lock(lockKey, 20, 60, new DefaultLockCallback<Boolean>(false, false) {
            @Override
            public Boolean handleObtainLock() {
                List<String> list = Static.systemSerialNumberService.generatePrepareSerialNumbers(finalApp,moduleCode);
                for (String serial : list) {
                    CacheUtils.getCacheChannel().queuePush(region, serial);
                }
                return true;
            }
        });
        if (!flag) {
            return null;
        }
        value = CacheUtils.getCacheChannel().queuePop(region);
        return value;
    }

}
