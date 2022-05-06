/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.common.orm._enum.GenericEnumUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.eryansky.modules.sys._enum.YesOrNo;

import java.util.Calendar;
import java.util.Date;

/**
 * 系统更新日志
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-09
 */
public class VersionLog extends DataEntity<VersionLog> {

    public static final String FOLDER_VERSIONLOG = "VersionLog";
    public static final String DEFAULT_ID = "1";

    /**
     * 版本号
     */
    private String versionName;
    /**
     * 版本内部编号
     */
    private String versionCode;
    /**
     * 附件
     */
    private String fileId;
    /**
     * APP标识
     */
    private String app;
    /**
     * 更新类型 ${@link VersionLogType}
     */
    private String versionLogType;
    /**
     * 是否发布 {@link YesOrNo}
     */
    private String isPub;
    /**
     * 是否提示 {@link YesOrNo}
     */
    private String isTip;
    /**
     * 是否下架 {@link YesOrNo}
     */
    private String isShelf;
    /**
     * 发布时间
     */
    private Date pubTime;
    /**
     * 变更说明
     */
    private String remark;

    /**
     * 关键字
     */
    private String query;

    public VersionLog() {
        super();
    }

    public VersionLog(String id) {
        super(id);
    }

    @Override
    public void prePersist() {
        super.prePersist();
        this.pubTime = Calendar.getInstance().getTime();
        if(StringUtils.isBlank(app)){
            this.app = DEFAULT_ID;
        }
    }

    @Override
    public void preUpdate() {
        this.pubTime = Calendar.getInstance().getTime();
        if(StringUtils.isBlank(app)){
            this.app = DEFAULT_ID;
        }
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getVersionLogType() {
        return versionLogType;
    }

    public void setVersionLogType(String versionLogType) {
        this.versionLogType = versionLogType;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date updateTime) {
        this.pubTime = updateTime;
    }


    public String getIsPub() {
        return isPub;
    }

    public String getIsPubView() {
        return GenericEnumUtils.getDescriptionByValue(YesOrNo.class,isPub,isPub);
    }

    public void setIsPub(String isPub) {
        this.isPub = isPub;
    }

    public String getIsTip() {
        return isTip;
    }

    public void setIsTip(String isTip) {
        this.isTip = isTip;
    }

    public String getIsTipView() {
        return GenericEnumUtils.getDescriptionByValue(YesOrNo.class,isTip,isTip);
    }

    public String getIsShelf() {
        return isShelf;
    }

    public void setIsShelf(String isShelf) {
        this.isShelf = isShelf;
    }

    public String getIsShelfView() {
        return GenericEnumUtils.getDescriptionByValue(YesOrNo.class,isShelf,isShelf);
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 系统类型描述.
     */
    public String getVersionLogTypeView() {
        return GenericEnumUtils.getDescriptionByValue(VersionLogType.class,versionLogType,versionLogType);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return JsonMapper.getInstance().toJson(this);
    }
}
