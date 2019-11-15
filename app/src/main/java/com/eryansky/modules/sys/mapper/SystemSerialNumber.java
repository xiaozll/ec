/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys._enum.ResetType;
import com.eryansky.modules.sys._enum.YesOrNo;

/**
 * 序列号生成器
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-07-14
 */
public class SystemSerialNumber extends DataEntity<SystemSerialNumber> {

    public static final String DEFAULT_ID = "1";
    public static final String QUEUE_SYS_SERIAL = "queue_system_serial";
    /**
     * APP标识
     */
    private String app;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 模块编码
     */
    private String moduleCode;
    /**
     * 流水号配置模板
     */
    private String configTemplate;
    /**
     * 系列号最大值
     */
    private String maxSerial;
    /**
     * 重置类型 {@link ResetType}
     */
    private String resetType;
    /**
     * 是否自动增长标识 {@link YesOrNo}
     */
    private String isAutoIncrement;
    /**
     * 预生成流水号数量
     */
    private String preMaxNum;
    /**
     * 备注
     */
    private String remark;

    /**
     * 关键字
     */
    private String query;

    public SystemSerialNumber() {
        this.isAutoIncrement = YES;
        this.maxSerial = "0";
        this.preMaxNum = "1";
    }

    @Override
    public void prePersist() {
        super.prePersist();
        if(StringUtils.isBlank(app)){
            this.app = DEFAULT_ID;
        }
    }

    public SystemSerialNumber(String id) {
        super(id);
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getConfigTemplate() {
        return configTemplate;
    }

    public void setConfigTemplate(String configTemplate) {
        this.configTemplate = configTemplate;
    }

    public String getMaxSerial() {
        return maxSerial;
    }

    public void setMaxSerial(String maxSerial) {
        this.maxSerial = maxSerial;
    }

    public String getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(String isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }


    public String getIsAutoIncrementView() {
        YesOrNo e = YesOrNo.getByValue(isAutoIncrement);
        return null != e ? e.getDescription():isAutoIncrement;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public String getResetTypeView() {
        ResetType e = ResetType.getByValue(resetType);
        return null != e ? e.getDescription():resetType;
    }


    public String getPreMaxNum() {
        return preMaxNum;
    }

    public void setPreMaxNum(String preMaxNum) {
        this.preMaxNum = preMaxNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
