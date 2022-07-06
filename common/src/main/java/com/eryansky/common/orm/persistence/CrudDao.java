/**
 *  Copyright (c) 2012-2022 https://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.persistence;

import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

import java.util.List;

/**
 * DAO支持类实现
 * @author Eryan
 * @version 2014-05-16
 * @param <T>
 */
public interface CrudDao<T> extends BaseDao {

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	T get(String id);
	
	/**
	 * 获取单条数据
	 * @param entity
	 * @return
	 */
	T get(T entity);
	
	/**
	 * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setEntityPage(new Page<T>());
	 * @param entity
	 * @return
	 */
	List<T> findList(T entity);
	
	/**
	 * 查询所有数据列表
	 * @param entity
	 * @return
	 */
	List<T> findAllList(T entity);
	
	/**
	 * 查询所有数据列表
	 * @see public List<T> findAllList(T entity)
	 * @return
	 */
	@Deprecated
	List<T> findAllList();


	/**
	 * 动态查询（多个对象）
	 * @param selectStatement
	 * @return
	 */
	List<T> selectMany(SelectStatementProvider selectStatement);

	/**
	 * 动态查询（单个对象）
	 * @return
	 */
	T selectOne(SelectStatementProvider selectStatement);

	/**
	 * 插入数据
	 * @param entity
	 * @return
	 */
	int insert(T entity);
	
	/**
	 * 更新数据
	 * @param entity
	 * @return
	 */
	int update(T entity);
	
	/**
	 * 删除数据（一般为逻辑删除，更新status字段为1）
	 * @param id
	 * @see public int DELETE(T entity)
	 * @return
	 */
	@Deprecated
	int delete(String id);
	
	/**
	 * 删除数据（一般为逻辑删除，更新status字段为1）
	 * @param entity
	 * @return
	 */
	int delete(T entity);

	/**
	 * 删除数据（物理删除）
	 * @param id
	 * @return
	 */
	int clear(String id);
	/**
	 * 删除数据（物理删除）
	 * @param entity
	 * @return
	 */
	int clear(T entity);
    /**
     * 清空数据（物理删除）
     * @return
     */
    int clearAll();
}