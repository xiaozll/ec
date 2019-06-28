/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.modules.sys.dao.AreaDao;
import com.eryansky.modules.sys.mapper.Area;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区域Service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
@Service
public class AreaService extends TreeService<AreaDao, Area> {

	public List<Area> findAll(){
		return dao.findAllList(new Area());
	}

	public void save(Area area) {
		super.save(area);
	}

	public void delete(Area area) {
		super.delete(area);
	}

	public void deleteOwnerAndChilds(String id){
		dao.deleteOwnerAndChilds(new Area(id));
	}
	/**
	 * 根据编码查找
	 * @param code 编码
	 * @return
	 */
	public Area getByCode(String code){
        return dao.getByCode(code);
    }

	/**
	 * 查找区县及以上
	 * @return
	 */
	public List<Area> findAreaUp(){
		Area entity = new Area();
		return dao.findAreaUp(entity);
	}
	/**
	 * 查找区县及以下
	 * @return
	 */
	public List<Area> findAreaDown(String parentId){
		Parameter parameter = Parameter.newParameter();
		parameter.put("areaId",parentId);
		parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
		return dao.findAreaDown(parameter);
	}

	/**
	 * 查找本机以及以下
	 * @param parentId
	 * @return
	 */
	public List<Area> findOwnAndChild(String parentId){
		Parameter parameter = Parameter.newParameter();
		parameter.put("parentId",parentId);
		parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
		return dao.findOwnAndChild(parameter);
	}

	/**
	 * 查找所属下级
	 * @param parentId
	 * @return
	 */
	public List<Area> findByParentId(String parentId){
		Parameter parameter = Parameter.newParameter();
		parameter.put("parentId",parentId);
		parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
		return dao.findByParentId(parameter);
	}




}
