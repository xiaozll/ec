/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.modules.sys.vo.PasswordTip;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eryansky.modules.sys.mapper.UserPassword;
import com.eryansky.modules.sys.dao.UserPasswordDao;
import com.eryansky.core.orm.mybatis.service.CrudService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户密码修改记录 service
 *
 * @author eryan
 * @date 2018-05-08
 */
@Service
public class UserPasswordService extends CrudService<UserPasswordDao, UserPassword> {

    public UserPassword getLatestUserPasswordByUserId(String userId) {
        List<UserPassword> userPasswords = getUserPasswordsByUserId(userId, 1);
        return Collections3.isEmpty(userPasswords) ? null : userPasswords.get(0);
    }

    /**
     * 查询某个用户ID秘密修改记录
     * <br/>根据修改时间 降序排列
     *
     * @param userId  用户ID
     * @param maxSize 最大记录数 为null是查询所有
     * @return
     */
    public List<UserPassword> getUserPasswordsByUserId(String userId, Integer maxSize) {
        Page<UserPassword> page = new Page<UserPassword>();
        if (maxSize != null) {
            page.setPageSize(maxSize);
        }
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("userId", userId);
        parameter.put(BaseInterceptor.PAGE, page);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        return page.setResult(dao.findByUserId(parameter)).getResult();
    }

    /**
     * 新增修改密码记录
     *
     * @param user
     * @return
     */
    public UserPassword addUserPasswordUpdate(User user) {
        UserPassword userPassword = new UserPassword(user.getId(), user.getPassword());
        userPassword.setOriginalPassword(user.getOriginalPassword());
        this.save(userPassword);
        return userPassword;
    }

    /**
     * 检查密码策略
     * @param userId
     * @return
     */
    public PasswordTip checkPassword(String userId) {
        UserPassword userPassword = getLatestUserPasswordByUserId(userId);
        Calendar calendar = Calendar.getInstance();
        int userPasswordUpdateCycle = AppConstants.getUserPasswordUpdateCycle();
        calendar.add(Calendar.DATE, -userPasswordUpdateCycle);
        Date time = calendar.getTime();

        PasswordTip tip = new PasswordTip();
        if (userPassword == null) {
            tip.setMsg("您从未修改过登录秘密，请修改登录密码！");
            tip.setCode(PasswordTip.CODE_YES);
        } else if (time.compareTo(userPassword.getModifyTime()) > 0) {
            tip.setMsg("您已超过" + userPasswordUpdateCycle + "天没有修改登录密码，请修改登录密码！");
            tip.setCode(PasswordTip.CODE_TIME_OUT);
        }
        return tip;
    }
}
