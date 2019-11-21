/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.VersionLogService;
import com.eryansky.utils.AppUtils;

import static com.eryansky.utils.AppUtils.likeIOS;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2019-11-21
 */
public class VersionLogUtils {

    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    public static final class Static {
        private static VersionLogService versionLogService = SpringContextHolder.getBean(VersionLogService.class);
    }

    /**
     * 获取当前版本的更新说明
     *
     * @return
     */
    public static VersionLog getLatestVersionLog() {
        String userAgent = UserAgentUtils.getHTTPUserAgent(SpringMVCHolder.getRequest());
        if (AppUtils.likeAndroid(userAgent)) {
            return Static.versionLogService.getLatestVersionLog(VersionLogType.Android.getValue());
        } else if (likeIOS(userAgent)) {
            return Static.versionLogService.getLatestVersionLog(VersionLogType.iPhone.getValue());
        }
        return Static.versionLogService.getLatestVersionLog(VersionLogType.Server.getValue());

    }

    /**
     * 获取当前版本的更新说明
     *
     * @param versionLogType {@link com.eryansky.modules.sys._enum.VersionLogType}
     * @return
     */
    public static VersionLog getLatestVersionLog(String versionLogType) {
        return Static.versionLogService.getLatestVersionLog(versionLogType);
    }
}
