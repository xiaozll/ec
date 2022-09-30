/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.ApplicationSessionContext;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.util.SerializationUtils;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.utils.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 系统监控
 *
 * @author Eryan
 * @date 2016-10-28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/systemMonitor")
public class SystemMonitorController extends SimpleController {

    /**
     * 系统信息
     *
     * @return
     */
    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控", logType = LogType.access, logging = "!#isAjax")
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "")
    public String list(HttpServletRequest request, HttpServletResponse response) {
        if (WebUtils.isAjaxRequest(request)) {
            ServerStatus serverStatus = null;
            try {
                serverStatus = SigarUtil.getServerStatus();
                return renderString(response, Result.successResult().setObj(serverStatus));
            } catch (Exception e) {
                logger.error(e.getMessage());
                return renderString(response, Result.errorResult().setMsg(e.getMessage()));
            }
        }
        return "modules/sys/systemMonitor";
    }


    /**
     * 系统监控-缓存管理
     *
     * @return
     */
    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控-缓存管理", logType = LogType.access, logging = "!#isAjax")
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "cache")
    public String cache(HttpServletRequest request, Model uiModel, HttpServletResponse response) {
        Page<Map<String, Object>> page = new Page<>(request, response);
        if (WebUtils.isAjaxRequest(request)) {
            Collection<CacheChannel.Region> regions = CacheUtils.regions();
            List<CacheChannel.Region> list = AppUtils.getPagedList(Collections3.union(regions, Collections.emptyList()), page.getPageNo(), page.getPageSize());
            List<Map<String, Object>> dataList = Lists.newArrayList();
            for (CacheChannel.Region r : list) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("name", r.getName());
                map.put("size", r.getSize());
                map.put("ttl", r.getTtl());
                map.put("keys", CacheUtils.keySize(r.getName()));
                dataList.add(map);
            }
            page.setTotalCount(regions.size());
            page.setResult(dataList);
            return renderString(response, page);
        }
        uiModel.addAttribute("page", page);
        return "modules/sys/systemMonitor-cache";
    }

    /**
     * 系统监控-缓存管理
     *
     * @return
     */
    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控-缓存管理", logType = LogType.access, logging = "!#isAjax")
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "cacheDetail")
    public String cacheDetail(String region, Model uiModel, HttpServletRequest request, HttpServletResponse response) {
        Page<Map<String, Object>> page = new Page<>(request, response);
        if (WebUtils.isAjaxRequest(request)) {
            Collection<String> keys = CacheUtils.keys(region);
            page.setTotalCount(keys.size());
            List<String> pKeys = AppUtils.getPagedList(Collections3.union(keys, Collections.emptyList()), page.getPageNo(), page.getPageSize());
            List<Map<String, Object>> dataList = Lists.newArrayList();
            CacheChannel cacheChannel = CacheUtils.getCacheChannel();
            pKeys.forEach(key -> {
                Map<String, Object> map = Maps.newHashMap();
                map.put("key", key);
                map.put("keyEncodeUrl", EncodeUtils.urlEncode(key));
                map.put("ttl1", cacheChannel.ttl(region, key, 1));
                map.put("ttl2", cacheChannel.ttl(region, key, 2));
                dataList.add(map);
            });
            page.setResult(dataList);
            return renderString(response, page);
        }
        uiModel.addAttribute("region", region);
        uiModel.addAttribute("page", page);
        return "modules/sys/systemMonitor-cacheDetail";
    }

    /**
     * 系统监控-缓存管理
     *
     * @return
     */
    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控-缓存管理", logType = LogType.access, logging = "!#isAjax")
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "cacheKeyDetail")
    public String cacheKeyDetail(String region, String key, Model uiModel, HttpServletRequest request, HttpServletResponse response) {
        Object object = CacheUtils.get(region, key);
        try {
            uiModel.addAttribute("data", JsonMapper.getInstance().writeValueAsString(object));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            try {
                uiModel.addAttribute("data", new String(SerializationUtils.serialize(object)));
            } catch (IOException e1) {
                logger.error(e1.getMessage(), e1);
            }

        }
        uiModel.addAttribute("object", object);
        uiModel.addAttribute("region", region);
        uiModel.addAttribute("key", key);
        return "modules/sys/systemMonitor-cacheKeyDetail";
    }


    /**
     * 清空缓存
     *
     * @param region 缓存名称
     * @return
     */
    @Logging(value = "系统监控-清空缓存", logType = LogType.access)
    @RequiresPermissions("sys:systemMonitor:edit")
    @GetMapping(value = "clearCache")
    public String clearCache(String region, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        //清空ehcache缓存
        if (StringUtils.isNotBlank(region)) {
            CacheUtils.clearCache(region);
        } else {
            Collection<String> regions = CacheUtils.regionNames();
            logger.warn("regionNames:{}", JsonMapper.toJsonString(regions));
            for (String _cacheName : regions) {
                if (!ApplicationSessionContext.CACHE_SESSION.equals(_cacheName)) {//黑名单
                    CacheUtils.clearCache(_cacheName);
                }
            }
            //更新客户端缓存时间戳
            AppConstants.SYS_INIT_TIME = System.currentTimeMillis();
        }
        addMessage(redirectAttributes, "操作成功！");
        return "redirect:" + AppConstants.getAdminPath() + "/sys/systemMonitor/cache?repage";
    }


    /**
     * 清空缓存
     *
     * @param region 缓存名称
     * @return
     */
    @Logging(value = "系统监控-清空缓存", logType = LogType.access)
    @RequiresPermissions("sys:systemMonitor:edit")
    @GetMapping(value = "clearCacheKey")
    public String clearCacheKey(String region, String key, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        CacheUtils.remove(region, key);
        addMessage(redirectAttributes, "操作成功！");
        return "redirect:" + AppConstants.getAdminPath() + "/sys/systemMonitor/cacheDetail?region=" + region + "&repage";
    }


    /**
     * 系统监控-系统日志
     *
     * @param download 下载
     * @param request
     * @param response
     * @param uiModel
     * @return
     */
    @Logging(value = "系统监控-系统日志", logType = LogType.access, logging = "!#isAjax")
    @RequiresPermissions("sys:systemMonitor:view")
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "log")
    public String log(@RequestParam(value = "download", defaultValue = "false") boolean download,
                      Integer pageSize,
                      HttpServletRequest request, HttpServletResponse response, Model uiModel) {
        Result result = null;
        File file = null;
        if (download || WebUtils.isAjaxRequest(request)) {
            String _logPath = AppConstants.getLogPath(findLogFilePath());//读取配置文件配置的路径
            file = new File(_logPath);
        }
        if (download) {
            WebUtils.setDownloadableHeader(request, response, file.getName());
            try (OutputStream os = response.getOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(file);
                 BufferedInputStream is = new BufferedInputStream(fileInputStream)) {
                IOUtils.copy(is, os);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            return null;
        }
        if (WebUtils.isAjaxRequest(request)) {
            try {
                // 读取日志
                assert file != null;
                List<String> logs = FileUtils.readLines(file, "utf-8");
                List<String> showLogs = logs;
                StringBuilder log = new StringBuilder();
                Collections.reverse(logs);
                Page<String> page = new Page<>(request, response);
                page.setPageSize(pageSize == null ? 1000 : pageSize);//最大读取行数
                if (page.getPageSize() != Page.PAGESIZE_ALL) {
                    showLogs = Collections3.getPagedList(logs, page.getPageNo(), page.getPageSize());
                    page.setResult(showLogs);
                    page.setTotalCount(showLogs.size());
                }
                for (int i = showLogs.size() - 1; i >= 0; i--) {
                    String line = logs.get(i);
                    log.append(line.replace("\t", "&nbsp;")).append("<br>");
                }
                return renderString(response, Result.successResult().setMsg(log.toString()).setObj(page));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return renderString(response, Result.errorResult().setMsg(e.getMessage()));
            }
        }
        return "modules/sys/systemMonitor-log";
    }

    /**
     * 系统监控-缓存管理
     *
     * @return
     */
    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控-队列管理", logType = LogType.access, logging = "!#isAjax")
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "queue")
    public String queue(HttpServletRequest request, Model uiModel, HttpServletResponse response) {
        Page<Map<String, Object>> page = new Page<>(request, response);
        if (WebUtils.isAjaxRequest(request)) {
            Collection<CacheChannel.Region> regions = CacheUtils.getCacheChannel().queues();
            List<CacheChannel.Region> list = AppUtils.getPagedList(Collections3.union(regions, Collections.emptyList()), page.getPageNo(), page.getPageSize());
            List<Map<String, Object>> dataList = Lists.newArrayList();
            for (CacheChannel.Region r : list) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("name", r.getName());
                map.put("size", r.getSize());
                map.put("keys", CacheUtils.getCacheChannel().queueList(r.getName()).size());
                dataList.add(map);
            }
            page.setTotalCount(regions.size());
            page.setResult(dataList);
            return renderString(response, page);
        }
        uiModel.addAttribute("page", page);
        return "modules/sys/systemMonitor-queue";
    }


    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控-队列管理:队列数据", logType = LogType.access, logging = "!#isAjax")
    @GetMapping(value = "queueDetail")
    public String queueDetail(HttpServletRequest request, Model uiModel, String region, HttpServletResponse response) {
        Collection<String> queueList = CacheUtils.getCacheChannel().queueList(region);
        uiModel.addAttribute("region", region);
        uiModel.addAttribute("data", JsonMapper.toJsonString(queueList));
        return "modules/sys/systemMonitor-queueDetail";
    }

    @RequiresPermissions("sys:systemMonitor:edit")
    @Logging(value = "系统监控-队列管理:清空队列", logType = LogType.access, logging = "!#isAjax")
    @GetMapping(value = "queueClear")
    public String queueClear(HttpServletRequest request,RedirectAttributes redirectAttributes,String region, HttpServletResponse response) {
        CacheUtils.getCacheChannel().queueClear(region);
        addMessage(redirectAttributes, "操作成功！");
        return "redirect:" + AppConstants.getAdminPath() + "/sys/systemMonitor/queue?repage";
    }


    @RequiresPermissions("sys:systemMonitor:edit")
    @Logging(value = "系统监控-队列管理:清空队列", logType = LogType.access, logging = "!#isAjax")
    @GetMapping(value = "clearAllQueue")
    public String clearAllQueue(HttpServletRequest request,RedirectAttributes redirectAttributes,HttpServletResponse response) {
        Collection<CacheChannel.Region> regions = CacheUtils.getCacheChannel().queues();
        for (CacheChannel.Region r : regions) {
            CacheUtils.getCacheChannel().queueClear(r.getName());
        }
        addMessage(redirectAttributes, "操作成功！");
        return "redirect:" + AppConstants.getAdminPath() + "/sys/systemMonitor/queue?repage";
    }


    /**
     * 动态获取日志文件所在路径
     *
     * @return
     */
    private String findLogFilePath() {
        String canonicalPath = null;
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext(); ) {
                Appender<ILoggingEvent> appender = index.next();
                if (appender instanceof FileAppender) {
                    FileAppender fileAppender = (FileAppender) appender;
                    File file = new File(fileAppender.getFile());
                    try {
                        canonicalPath = file.exists() ? file.getCanonicalPath() : null;
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                    return canonicalPath;
                }
            }
        }
        logger.info("Log path {}", canonicalPath);
        return canonicalPath;
    }

}
