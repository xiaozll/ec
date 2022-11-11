/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;


import com.eryansky.common.orm._enum.GenericEnumUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.eryansky.core.orm.mybatis.entity.TreeEntity;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 资源/权限（菜单、功能）
 *
 * @author Eryan
 * @date 2018-05-08
 */
public class Resource extends TreeEntity<Resource> {

    /**
     * 资源编码
     */
    private String code;
    /**
     * 资源url地址
     */
    private String url;
    /**
     * 图标
     */
    private String iconCls;
    /**
     * 图标地址
     */
    private String icon;
    /**
     * 标记URL
     */
    private String markUrl;
    /**
     * 资源类型 ${@link ResourceType}
     */
    private String type;

    public Resource() {
    }

    public Resource(String id) {
        super(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public Resource getParent() {
        return parent;
    }

    @Override
    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getIconCls() {
        return this.iconCls;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setMarkUrl(String markUrl) {
        this.markUrl = markUrl;
    }

    public String getMarkUrl() {
        return this.markUrl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @JsonProperty(value = "_parentId")
    public String get_parentId() {
        String id = null;
        if (parent != null) {
            id = parent.getId().equals("0") ? null : parent.getId();
        }
        return id;
    }

    /**
     * 资源类型描述
     */
    public String getTypeView() {
        return GenericEnumUtils.getDescriptionByValue(ResourceType.class,type,type);
    }
}
