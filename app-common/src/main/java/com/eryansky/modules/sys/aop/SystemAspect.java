/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.aop;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.SystemService;
import com.eryansky.modules.sys.service.UserPasswordService;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切面
 *
 * @author eryan
 * @date 2018-05-10
 */
@Order(1)
@Component
@Aspect
public class SystemAspect implements InitializingBean, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(SystemAspect.class);

    @Autowired
    private SystemService systemService;

    /**
     * 同步到扩展机构表
     *
     * @param joinPoint 切入点
     * @param returnObj 返回值
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.sys.service.OrganService.saveOrgan(..)) || " +
            "execution(* com.eryansky.modules.sys.service.OrganService.deleteById(..))", returning = "returnObj")
    public void afterSyncOrganToExtend(JoinPoint joinPoint, Object returnObj) {
        if (returnObj != null) {
            Parameter parameter = Parameter.newParameter();
            if (returnObj instanceof String) {
                String id = (String) returnObj;
                parameter.put("id", id);
                Organ organ = OrganUtils.getOrgan(id);
                parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
                parameter.put("companyId", OrganUtils.getCompanyIdByRecursive(organ.getId()));
                parameter.put("companyCode", OrganUtils.getCompanyCodeByRecursive(organ.getId()));
                parameter.put("homeCompanyId", OrganUtils.getHomeCompanyIdByRecursive(organ.getId()));
                parameter.put("homeCompanyCode", OrganUtils.getHomeCompanyCodeByRecursive(organ.getId()));
                Integer level = StringUtils.isNotBlank(organ.getParentIds()) ? organ.getParentIds().split(",").length : null;
                parameter.put("treeLevel", level);

                systemService.syncOrganToExtend(parameter);
            } else if (returnObj instanceof Organ) {
                Organ organ = (Organ) returnObj;
                parameter.put("id", organ.getId());
                parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
                parameter.put("companyId", OrganUtils.getCompanyIdByRecursive(organ.getId()));
                parameter.put("companyCode", OrganUtils.getCompanyCodeByRecursive(organ.getId()));
                parameter.put("homeCompanyId", OrganUtils.getHomeCompanyIdByRecursive(organ.getId()));
                parameter.put("homeCompanyCode", OrganUtils.getHomeCompanyCodeByRecursive(organ.getId()));
                Integer level = StringUtils.isNotBlank(organ.getParentIds()) ? organ.getParentIds().split(",").length : null;
                parameter.put("treeLevel", level);
                systemService.syncOrganToExtend(parameter);
            }
        } else {
            systemService.syncOrganToExtend();
        }

    }


    /**
     * 同步到扩展机构表
     *
     * @param joinPoint 切入点
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.sys.service.OrganService.deleteOwnerAndChilds(..))")
    public void afterSyncOrganToExtend(JoinPoint joinPoint) {
        systemService.syncOrganToExtend();

    }

    /**
     * 同步到扩展机构表
     *
     * @param joinPoint 切入点
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.sys.service.UserService.updatePasswordByUserId(..)) " +
            "|| execution(* com.eryansky.modules.sys.service.UserService.updatePasswordByLoginName(..))",returning = "returnObj")
    public void afterUserPasswordUpdate(JoinPoint joinPoint, User returnObj) {
       if(null == returnObj){
           return;
       }
        UserUtils.addUserPasswordUpdate(returnObj);

    }


    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
