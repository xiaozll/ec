/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;

import com.eryansky.core.orm.mybatis.dao.TreeDao;
import com.eryansky.modules.sys.mapper.Resource;

import java.util.List;

/**
 * 资源表
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@MyBatisDao
public interface ResourceDao extends TreeDao<Resource> {

    Resource getByCode(Parameter parameter);

    Integer getMaxSort();

    List<Resource> findOwnAndChilds(Parameter parameter);

    List<Resource> findChilds(Parameter parameter);


    List<Resource> findChild(Parameter parameter);

    List<Resource> findQuery(Parameter parameter);

    List<Resource> findCustomQuery(Parameter parameter);


    List<Resource> findResourcesByUserId(Parameter parameter);

    List<String> findResourceIdsByUserId(Parameter parameter);


    List<Resource> findResourcesByRoleId(Parameter parameter);

    List<String> findResourceIdsByRoleId(Parameter parameter);

    List<Resource> findAuthorityResourcesByUserId(Parameter parameter);

    int deleteOwnerAndChilds(Resource entity);

    /**
     * 自定义SQL查询
     *
     * @param parameter
     * @return
     */
    List<Resource> findByWhereSQL(Parameter parameter);

    /**
     * 自定义SQL查询
     *
     * @param parameter
     * @return
     */
    List<Resource> findBySql(Parameter parameter);
}
