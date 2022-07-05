/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.Log;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 日志DAO接口
 *
 * @author eryan
 * @version 2015-9-26
 */
@MyBatisDao
public interface LogDao extends CrudDao<Log> {

    List<Log> findQueryList(Parameter parameter);

    /**
     * 插入到历史表
     *
     * @return
     */
    int insertToHistory(Parameter parameter);

    /**
     * 清空有效期之外的日志
     *
     * @return
     */
    int clearInvalidLog(Map<String, Object> parameter);

    /**
     * 用户登录测试
     *
     * @param parameter
     * @return
     */
    Long getUserLoginCount(Map<String, Object> parameter);


    /**
     * 查询标题为null的数据
     *
     * @return
     */
    List<Log> findNullData();


    /**
     * 根据“module”查询标题不为null的数据
     *
     * @param module
     * @return
     */
    Log getNotNullData(String module);


    /**
     * 员工登录统计
     */

    List<Map<String, Object>> getLoginStatistics(Map<String, Object> parameter);


    /**
     * 模块访问统计
     */
    List<Map<String, Object>> getModuleStatistics(Map<String, Object> parameter);

    /**
     * 每天访问数据
     *
     * @param parameter
     * @return
     */
    List<Map<String, Object>> getDayLoginStatistics(Map<String, Object> parameter);

}
