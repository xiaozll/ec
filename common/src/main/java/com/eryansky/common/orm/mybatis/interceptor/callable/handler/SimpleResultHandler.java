/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.mybatis.interceptor.callable.handler;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单游标结果集
 * @author eryan
 * @date 2016-09-21
 */
public class SimpleResultHandler implements CallableResultHandler {

    public Object proceed(List<ParameterMapping> parameterMappings, Map<String, Object> result) {
        List list = new ArrayList();
        Map<String, Object> map = new HashMap<String, Object>();
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (ParameterMode.OUT.equals(parameterMapping.getMode()) || ParameterMode.INOUT.equals(parameterMapping.getMode())) {
                list.add(result.get(parameterMapping.getProperty()));
                map.put(parameterMapping.getProperty(), result.get(parameterMapping.getProperty()));
            }
        }
        if (list.size() == 1) {
            Object value = list.get(0);
            if (value instanceof List) {
                return value;
            }
            return list;
        }
        list.clear();
        list.add(map);
        return list;
    }
}
