/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;

import com.eryansky.modules.sys.mapper.User;

import java.util.List;

/**
 * 用户表
 *
 * @author Eryan
 * @date 2018-05-08
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {

    List<User> findByIds(Parameter parameter);

    List<User> findByLoginNames(Parameter parameter);

    List<User> findByCodes(Parameter parameter);

    User getUserByLoginName(Parameter parameter);

    User getUserByMobile(Parameter parameter);

    User getUserByIdOrMobile(Parameter parameter);

    User getUserByIdOrLoginName(Parameter parameter);

    User getUserByLoginNameOrMobile(Parameter parameter);

    List<User> findByCode(Parameter parameter);

    List<User> findByName(Parameter parameter);

    List<User> findByLoginNameOrCode(Parameter parameter);

    List<User> findByLoginNameOrMobile(Parameter parameter);

    Integer getMaxSort();

    List<User> findQuery(Parameter parameter);

    List<User> findLoginUser(Parameter parameter);

    List<String> findAllNormalUserIds(Parameter parameter);

    List<User> findWithInclude(Parameter parameter);


    List<User> findAllNormalWithExclude(Parameter parameter);

    List<User> findUsersByIds(Parameter parameter);

    List<User> findUsersByOrgan(Parameter parameter);

    List<User> findUsersByOrganIds(Parameter parameter);

    List<String> findUsersIdsByOrganIds(Parameter parameter);

    List<String> findUsersLoginNamesByOrganIds(Parameter parameter);


    List<User> findOrganUsers(Parameter parameter);

    Integer findOrganUserCount(Parameter parameter);

    List<String> findOrganUserIds(Parameter parameter);

    List<User> findOrganDefaultUsers(Parameter parameter);

    List<String> findOrganDefaultUserIds(Parameter parameter);


    List<User> findUsersByCompanyId(Parameter parameter);

    List<String> findUserIdsByCompanyId(Parameter parameter);


    List<User> findOwnerAndChildsUsers(Parameter parameter);

    List<String> findOwnerAndChildsUsersIds(Parameter parameter);

    List<User> findUsersByRole(Parameter parameter);

    List<User> findUsersByRoleId(Parameter parameter);

    List<String> findUserIdsByRoleId(Parameter parameter);

    List<User> findUsersByPost(Parameter parameter);


    List<String> findUserIdsByPost(Parameter parameter);


    List<User> findListByPostAndOrgan(Parameter parameter);

    List<String> findUserIdsByPostAndOrgan(Parameter parameter);

    List<String> findUserLoginNamesByPostAndOrgan(Parameter parameter);

    List<User> findOwnerAndChildsByPostAndOrgan(Parameter parameter);

    /**
     * 删除用户机构关联信息
     *
     * @param parameter id:用户ID
     */
    int deleteUserOrgansByUserId(Parameter parameter);

    /**
     * 插入用户机构关联信息
     *
     * @param parameter id:用户ID ids:机构IDS
     */
    int insertUserOrgans(Parameter parameter);


    /**
     * 删除用户岗位关联信息
     *
     * @param parameter id:用户ID
     */
    @Deprecated
    int deleteUserPostsByUserId(Parameter parameter);

    /**
     * 删除用户岗位关联信息
     *
     * @param parameter id:用户ID organId:机构ID
     */
    int deleteUserPostsByUserIdAndOrganId(Parameter parameter);

    /**
     * 删除用户不在这些部门下的岗位信息
     *
     * @param parameter
     * @return
     */
    int deleteNotInUserOrgansPostsByUserId(Parameter parameter);

    /**
     * 插入用户岗位关联信息
     *
     * @param parameter id:用户ID ids:岗位IDS
     */
    int insertUserPosts(Parameter parameter);


    /**
     * 删除用户角色关联信息
     *
     * @param parameter id:用户ID
     */
    int deleteUserRolesByUserId(Parameter parameter);

    /**
     * 插入用户角色关联信息
     *
     * @param parameter id:用户ID ids:用户IDS
     */
    int insertUserRoles(Parameter parameter);


    /**
     * 根据资源ID查找
     * @param parameter resourceId:资源ID
     * @return
     */
    List<User> findUsersByResourceId(Parameter parameter);

    /**
     * 删除用户资源关联信息
     *
     * @param parameter id:用户ID
     */
    int deleteUserResourcesByUserId(Parameter parameter);

    /**
     * 删除用户资源关联信息
     *
     * @param parameter userId:用户ID resourceId:资源ID
     */
    int deleteUserResourceByResourceIdAndUserId(Parameter parameter);

    /**
     * 插入用户资源关联信息
     *
     * @param parameter id:用户ID ids:资源IDS
     */
    int insertUserResources(Parameter parameter);

    /**
     * 修改密码
     * @param model
     * @return
     */
    int updatePassword(User model);


    /**
     * 自定义SQL查询
     *
     * @param parameter
     * @return
     */
    List<User> findByWhereSQL(Parameter parameter);

    /**
     * 自定义SQL查询
     *
     * @param parameter
     * @return
     */
    List<User> findBySql(Parameter parameter);

}
