/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * DAO支持类实现
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 * @param <T>
 */
public interface PCrudDao<T,PK extends Serializable> extends BaseDao {

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	T get(PK id);
	
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
	 * @see #findAllList(T entity)
	 * @return
	 */
	@Deprecated
	List<T> findAllList();
	
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
	 * @see #delete(T entity)
	 * @return
	 */
	@Deprecated
	int delete(PK id);
	
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
	int clear(PK id);

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