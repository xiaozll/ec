/**
 *  Copyright (c) 2012-2022 https://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.persistence.PCrudDao;
import com.eryansky.core.orm.mybatis.entity.PBaseEntity;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.entity.PDataEntity;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * Service基类
 * @author eryan
 * @version 2014-05-16
 */
public abstract class PCrudService<D extends PCrudDao<T,PK>, T extends PBaseEntity<T,PK>,PK extends Serializable> extends BaseService {

	/**
	 * 持久层对象
	 */
	@Autowired
	protected D dao;


	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(PK id) {
		return dao.get(id);
	}

	/**
	 * 获取单条数据
	 * @param entity
	 * @return
	 */
	public T get(T entity) {
		return dao.get(entity);
	}

	/**
	 * 查询列表数据
	 * @param entity
	 * @return
	 */
	public List<T> findList(T entity) {
		return dao.findList(entity);
	}

	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @param entity
	 * @return
	 */
	public Page<T> findPage(Page<T> page, T entity) {
		entity.setEntityPage(page);
		page.setResult(dao.findList(entity));
		return page;
	}

	/**
	 * 动态查询（多个对象）
	 * @param selectStatement
	 * @return
	 */
	public List<T> selectMany(SelectStatementProvider selectStatement){
		return dao.selectMany(selectStatement);
	}

	/**
	 * 动态查询（单个对象）
	 * @return
	 */
	public T selectOne(SelectStatementProvider selectStatement){
		return dao.selectOne(selectStatement);
	}

	/**
	 * 保存数据（插入或更新）
	 * @param entity
	 */
	public void save(T entity) {
		if (entity.getIsNewRecord()){
			entity.prePersist();
			dao.insert(entity);
		}else{
			entity.preUpdate();
			dao.update(entity);
		}
	}

	/**
	 * 删除数据（逻辑删除）
	 * @param entity
	 */
	public void delete(T entity) {
		entity.preUpdate();
		dao.delete(entity);
	}


	/**
	 * 删除数据（逻辑删除）
	 * @param id
	 */
	@Deprecated
	public void delete(PK id) {
		dao.delete(id);
	}

	/**
	 * 删除或还原删除数据
	 * @param entity
	 * @param isRe 是否还原
	 */
	public void delete(T entity, Boolean isRe) {
		if(isRe != null && isRe){
			if(entity instanceof PDataEntity){
				PDataEntity dataEntity = (PDataEntity) entity;
				dataEntity.setStatus(DataEntity.STATUS_NORMAL);
			}
			save(entity);
		}else{
			delete(entity);
		}
	}

	/**
	 * 删除数据（物理删除）
	 * @param entity
	 */
	public void clear(T entity) {
		dao.clear(entity);
	}

	/**
	 * 删除数据（物理删除）
	 * @param id
	 */
	public void clear(PK id) {
		dao.clear(id);
	}

	/**
	 * 清空数据（物理删除）
	 */
	public void clearAll() {
		dao.clearAll();
	}
}
