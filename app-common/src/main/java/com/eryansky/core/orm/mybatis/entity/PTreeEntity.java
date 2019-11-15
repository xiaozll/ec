/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.entity;

import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 树形Entity基类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 */
public abstract class PTreeEntity<T, PK extends Serializable> extends PDataEntity<T,PK> {

	private static final long serialVersionUID = 1L;

	protected T parent;	// 父级编号
	protected String parentIds; // 所有父级编号
	protected String name; 	// 名称
	protected Integer sort;		// 排序

	public PTreeEntity() {
		super();
		this.sort = 30;
	}

	public PTreeEntity(PK id) {
		super(id);
	}
	
	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	@JsonBackReference
	@NotNull
	public abstract T getParent();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	public abstract void setParent(T parent);

	@Length(min=1, max=2000)
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public PK getParentId() {
		PK parentId = null;
		if (parent != null){
			parentId = ReflectionUtils.getFieldValue(parent, "id");
		}
		return parentId != null ? parentId : (PK)(getId()  instanceof Long ? Long.valueOf(0):"0");
	}
	
}
