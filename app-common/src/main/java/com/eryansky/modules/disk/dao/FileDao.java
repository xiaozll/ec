/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;

import com.eryansky.modules.disk.mapper.File;

import java.util.List;

/**
 *
 * @author Eryan
 * @date 2018-05-04
 */
@MyBatisDao
public interface FileDao extends CrudDao<File> {

    /**
     * 根据文件夹ID级联删除文件（包含下级文件夹）
     * @param entity
     * @return
     */
    int deleteCascadeByFolderId(File entity);

    List<File> findByCode(Parameter parameter);

    List<File> findFilesByIds(Parameter parameter);


    List<File> findAdvenceQueryList(Parameter parameter);

    Long countFileSize(Parameter parameter);

    /**
     * 查找文件夹下所有文件（不包含下级文件夹的文件）
     * @param parameter
     * @return
     */
    List<File> findFolderFiles(Parameter parameter);

    /**
     * 查找文件夹下所有文件（包含下级文件夹的文件）
     * @param parameter
     * @return
     */
    List<File> findOwnerAndChildsFolderFiles(Parameter parameter);

    /**
     * 查找文件夹下所有文件IDS（包含下级文件夹的文件）
     * @param parameter
     * @return
     */
    List<String> findOwnerAndChildsIdsFolderFiles(Parameter parameter);
}
