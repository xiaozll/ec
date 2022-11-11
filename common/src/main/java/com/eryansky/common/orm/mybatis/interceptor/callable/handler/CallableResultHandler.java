/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.mybatis.interceptor.callable.handler;

import org.apache.ibatis.mapping.ParameterMapping;

import java.util.List;
import java.util.Map;

/**
 * 结果处理器
 * @author Eryan
 * @date 2016-09-21
 */
public interface CallableResultHandler {

    Object proceed(List<ParameterMapping> parameterMappings, Map<String, Object> result);

}
