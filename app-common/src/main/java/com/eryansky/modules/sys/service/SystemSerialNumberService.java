/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys._enum.ResetType;
import com.eryansky.modules.sys.dao.SystemSerialNumberDao;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.sn.GeneratorConstants;
import com.eryansky.modules.sys.sn.SNGenerateApp;
import com.eryansky.utils.AppDateUtils;
import com.eryansky.utils.CacheUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-07-14
 */
@Service
public class SystemSerialNumberService extends CrudService<SystemSerialNumberDao, SystemSerialNumber> {


    @Override
    public Page<SystemSerialNumber> findPage(Page<SystemSerialNumber> page, SystemSerialNumber entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }

    /**
     * 乐观锁更新方式
     *
     * @param entity
     * @return 返回更新数 0：更新失败 1：更新成功
     */
    public void updateByVersion(SystemSerialNumber entity) {
        int result = dao.updateByVersion(entity);
        if (result == 0) {
            throw new ServiceException("乐观锁更新失败," + entity.toString());
        }
    }

    /**
     * 根据模块编码查找
     *
     * @param moduleCode
     * @return
     */
    public SystemSerialNumber getByCode(String moduleCode) {
        return getByCode(SystemSerialNumber.DEFAULT_ID,moduleCode);
    }


    /**
     * 根据模块编码查找
     *
     * @param app APP标识
     * @param moduleCode
     * @return
     */
    public SystemSerialNumber getByCode(String app,String moduleCode) {
        SystemSerialNumber entity = new SystemSerialNumber();
        entity.setApp(app);
        entity.setModuleCode(moduleCode);
        return dao.getByCode(entity);
    }

    /**
     * 查询所有序列号配置信息
     */
    public List<SystemSerialNumber> findAll() {
        SystemSerialNumber entity = new SystemSerialNumber();
        return dao.findAllList(entity);
    }

    /**
     * 根据模块code生成预数量的序列号存放到Map中
     *
     * @param moduleCode 模块code
     * @return
     */
    public List<String> generatePrepareSerialNumbers(String app,String moduleCode) {
        SystemSerialNumber entity = getByCode(app,moduleCode);
        int version = entity.getVersion();
        /** 预生成数量 */
        int prepare = StringUtils.isNotBlank(entity.getPreMaxNum()) ? Integer.valueOf(entity.getPreMaxNum()) : 1;
        /** 数据库存储的当前最大序列号 **/
        long maxSerialInt = StringUtils.isNotBlank(entity.getMaxSerial()) ? Integer.valueOf(entity.getMaxSerial()) : 0;
        //临时List变量
        List<String> resultList = new ArrayList<String>(prepare);
        for (int i = 0; i < prepare; i++) {
            SNGenerateApp snGenerateApp = new SNGenerateApp();
            Map map = new HashMap(); //设定参数
            map.put(GeneratorConstants.PARAM_MODULE_CODE, moduleCode);
            map.put(GeneratorConstants.PARAM_MAX_SERIAL, maxSerialInt + "");
            String formatSerialNum = snGenerateApp.generateSN(entity.getConfigTemplate(), map);
            maxSerialInt++;
            //更新数据
            entity.setMaxSerial(maxSerialInt + "");
            updateByVersion(entity);
            version++;
            entity.setVersion(version);
            resultList.add(formatSerialNum);
        }
        return resultList;
    }


    /**
     * 年度重置序列号
     */
    public void resetSerialNumber() {
        List<SystemSerialNumber> numberList = this.findAll();
        Date now = null;
        try {
            now = DateUtils.parseDate(DateUtils.getDate(), DateUtils.DATE_FORMAT);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        for (SystemSerialNumber systemSerialNumber : numberList) {
            boolean flag = false;
            if (ResetType.Day.getValue().equals(systemSerialNumber.getResetType())) {
                flag = true;
            } else if (ResetType.Month.getValue().equals(systemSerialNumber.getResetType())) {
                flag = AppDateUtils.getCurrentMonthStartTime().equals(now);
            }
//            else if(ResetType.Quarter.getValue().equals(systemSerialNumber.getResetType())){
//                flag = AppDateUtils.getCurrentQuarterStartTime().equals(now);
//            }
            else if (ResetType.Year.getValue().equals(systemSerialNumber.getResetType())) {
                flag = AppDateUtils.getCurrentYearStartTime().equals(now);
            }
            if (flag) {
                systemSerialNumber.setMaxSerial("0");
                systemSerialNumber.setVersion(0);
                this.save(systemSerialNumber);
                //清空缓存
                String region = SystemSerialNumber.QUEUE_SYS_SERIAL + ":" + systemSerialNumber.getModuleCode();
                CacheUtils.getCacheChannel().queueClear(region);
            }
        }
    }


    /**
     * 清空缓存(指定key)
     */
    public void clearCacheByModuleCode(String moduleCode) {
        String region = SystemSerialNumber.QUEUE_SYS_SERIAL + ":" + moduleCode;
        CacheUtils.getCacheChannel().queueClear(region);
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        List<SystemSerialNumber> numberList = this.findAll();
        for (SystemSerialNumber systemSerialNumber : numberList) {
            //清空缓存
            String region = SystemSerialNumber.QUEUE_SYS_SERIAL + ":" + systemSerialNumber.getModuleCode();
            CacheUtils.getCacheChannel().queueClear(region);
        }
    }
}
