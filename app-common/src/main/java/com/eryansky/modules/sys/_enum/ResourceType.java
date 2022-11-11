/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys._enum;

import com.eryansky.common.orm._enum.GenericEnumUtils;
import com.eryansky.common.orm._enum.IGenericEnum;

/**
 * 资源类型标识 枚举类型.
 * <br>菜单(0) 功能(1)
 *
 * @author Eryan
 * @date 2013-01-28 上午10:48:23
 */
public enum ResourceType implements IGenericEnum<ResourceType> {
    /**
     * 应用(10)
     */
    app("10", "应用"),
    /**
     * 菜单(0)
     */
    menu("0", "菜单"),
    /**
     * 按钮(1)
     */
    function("1", "功能"),
    /**
     * CMS栏目资源(21)
     */
    CMS("21", "栏目");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    ResourceType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取描述信息
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static ResourceType getByValue(String value) {
        return GenericEnumUtils.getByValue(ResourceType.class,value);
    }

    public static ResourceType getByDescription(String description) {
        return GenericEnumUtils.getByDescription(ResourceType.class,description);
    }

}