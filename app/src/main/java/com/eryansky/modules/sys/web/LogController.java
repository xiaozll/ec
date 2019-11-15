/**
 * Copyright (c) 2012-2013 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.excelTools.CsvUtils;
import com.eryansky.core.excelTools.ExcelUtils;
import com.eryansky.core.excelTools.JsGridReportBase;
import com.eryansky.core.excelTools.TableData;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.security.annotation.RequiresRoles;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 日志
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-12-8 下午5:13
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/log")
public class LogController extends SimpleController {

    @Autowired
    private LogService logService;

    @ModelAttribute("model")
    public Log get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return logService.get(id);
        } else {
            return new Log();
        }
    }

    /**
     * 日志管理
     * @param type {@link LogType}
     * @param userInfo
     * @param query
     * @param startTime
     * @param endTime
     * @param export
     * @param uiModel
     * @param request
     * @param response
     * @return
     */
    @Logging(value = "日志管理", logType = LogType.access)
    @RequiresRoles(value = AppConstants.ROLE_SYSTEM_MANAGER)
    @RequestMapping(value = {""})
    public String list(String type,
                       String userInfo,
                       String query,
                       Date startTime,
                       Date endTime,
                       @RequestParam(value = "export", defaultValue = "false") Boolean export,
                       Model uiModel, HttpServletRequest request, HttpServletResponse response) {
        Page<Log> page = new Page<>(request);
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Date _startTime = null != startTime ? startTime : Calendar.getInstance().getTime();
        if (WebUtils.isAjaxRequest(request) || export) {
            if (export) {
                page.setPageSize(Page.PAGESIZE_ALL);
            }
            page = logService.findQueryPage(page, type, userInfo, query, _startTime, endTime, true);
            if (export) {
                List<Object[]> data = Lists.newArrayList();
                page.getResult().forEach(o -> {
                    data.add(new Object[]{o.getTypeView(), o.getTitle(), o.getUserCompanyName(), o.getUserOrganName(), o.getUserName(), o.getIp(), o.getDeviceType(), o.getModule(), DateUtils.formatDateTime(o.getOperTime()), o.getActionTime()});
                });


                String title = "审计日志-" + DateUtils.getCurrentDate();
                //Sheet2
                String[] hearders = new String[]{"日志类型", "标题", "单位", "部门", "姓名", "IP地址", "设备", "模块", "操作时间", "操作耗时(ms)"};//表头数组

                if (page.getResult().size() < 65531) {
                    //导出Excel
                    try {
                        TableData td = ExcelUtils.createTableData(data, ExcelUtils.createTableHeader(hearders, 0), null);
                        td.setSheetTitle(title);
                        JsGridReportBase report = new JsGridReportBase(request, response);
                        report.exportToExcel(title, sessionInfo.getName(), td);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    //导出CSV
                    try {
                        CsvUtils.exportToExcel(title, hearders, data, request, response);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                return null;
            }

            Datagrid<Log> dg = new Datagrid<>(page.getTotalCount(), page.getResult());
            String json = JsonMapper.getInstance().toJsonWithExcludeProperties(dg, Log.class, new String[]{"exception"});
            return renderString(response, json, WebUtils.JSON_TYPE);
        }
        uiModel.addAttribute("startTime", DateUtils.formatDate(_startTime));
        return "modules/sys/log";
    }


    /**
     * 日志明细信息
     * @param log
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"detail"})
    @ResponseBody
    public Result detail(@ModelAttribute("model") Log log) {
        return Result.successResult().setObj(log);
    }


    /**
     * 清除日志
     *
     * @return
     * @throws Exception
     */
    @Logging(value = "日志管理-删除日志", logType = LogType.access)
    @RequiresRoles(value = AppConstants.ROLE_SYSTEM_MANAGER)
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        logService.deleteByIds(ids);
        return Result.successResult();
    }

    /**
     * 清空所有日志
     *
     * @return
     * @throws Exception
     */
    @Logging(value = "日志管理-清除日志", logType = LogType.access)
    @RequiresRoles(value = AppConstants.ROLE_SYSTEM_MANAGER)
    @RequestMapping(value = {"removeAll"})
    @ResponseBody
    public Result removeAll() {
        logService.clearAll();
        return Result.successResult();
    }

    /**
     * 日志类型下拉列表.
     */
    @RequestMapping(value = {"logTypeCombobox"})
    @ResponseBody
    public List<Combobox> logTypeCombobox(String selectType) {
        List<Combobox> cList = Lists.newArrayList();
        Combobox selectCombobox = SelectType.combobox(selectType);
        if (selectCombobox != null) {
            cList.add(selectCombobox);
        }

        LogType[] lts = LogType.values();
        for (int i = 0; i < lts.length; i++) {
            Combobox combobox = new Combobox();
            combobox.setValue(lts[i].getValue());
            combobox.setText(lts[i].getDescription());
            cList.add(combobox);
        }
        return cList;
    }

    /**
     * 数据修复 title
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"dataAutoFix"})
    @ResponseBody
    public Result dataAutoFix() {
        logService.dataAutoFix();
        return Result.successResult();
    }
}
