/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.eryansky.modules.sys.utils.UserUtils;
//import com.eryansky.modules.weixin.utils.WeixinUtils;
import com.eryansky.utils.AppConstants;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.notice._enum.MessageMode;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.dao.MessageDao;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.notice.mapper.MessageSender;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14
 */
@Service
public class MessageService extends CrudService<MessageDao, Message> {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MessageReceiveService messageReceiveService;
    @Autowired
    private UserService userService;


    public Page<Message> findQueryPage(Page<Message> page, String appId,String userId,String status, Date startTime, Date endTime, boolean isDataScopeFilter, Map<String,Object> params) {
        Parameter parameter = Parameter.newParameter();
        if(isDataScopeFilter){
            parameter.put("dsf", super.dataScopeFilter(UserUtils.getUser(userId), "o", "u"));//数据权限控制
        }
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put(BaseInterceptor.PAGE, page);
        parameter.put("appId",appId);
        parameter.put("userId",userId);
        parameter.put("status",StringUtils.isBlank(status) ? DataEntity.STATUS_NORMAL:status);
        parameter.put("startTime", DateUtils.format(startTime,DateUtils.DATE_TIME_FORMAT));
        parameter.put("endTime", DateUtils.format(endTime,DateUtils.DATE_TIME_FORMAT));
        if (null != params) {
            params.forEach(parameter::putIfAbsent);
        }
        page.setResult(dao.findQueryList(parameter));
        return page;
    }

    /**
     *
     * @param page
     * @param appId
     * @param userId 分级授权用户ID
     * @param startTime
     * @param endTime
     * @param params
     * @return
     */
    public Page<Message> findPage(Page<Message> page, String appId, String userId, Date startTime, Date endTime, Map<String,Object> params) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put(BaseInterceptor.PAGE, page);
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("appId",appId);
        parameter.put("startTime", DateUtils.format(startTime,DateUtils.DATE_TIME_FORMAT));
        parameter.put("endTime", DateUtils.format(endTime,DateUtils.DATE_TIME_FORMAT));
        parameter.put("userId",userId);
        if(StringUtils.isNotBlank(userId) ){
            parameter.put("dsf", super.dataScopeFilter(UserUtils.getUser(userId), "o", "u"));//数据权限控制
        }

        if (null != params) {
            params.forEach(parameter::putIfAbsent);
        }
        page.setResult(dao.findQueryList(parameter));
        return page;
    }


    /**
     * 删除
     *
     * @param entity
     * @param isRe   是否恢复删除
     */
    public void delete(Message entity, Boolean isRe) {
        if (isRe != null && isRe) {
            entity.setStatus(Message.STATUS_NORMAL);
            super.save(entity);
        } else {
            super.delete(entity);
        }
    }

    /**
     * 保存并发送
     *
     * @param message
     * @param messageReceiveObjectType {@link MessageReceiveObjectType}
     * @param receiveObjectIds
     */
    public Message saveAndSend(Message message, MessageReceiveObjectType messageReceiveObjectType, List<String> receiveObjectIds) {
        if (Collections3.isEmpty(receiveObjectIds)) {
            throw new SystemException("未定义参数[receiveObjectIds]");
        }

        message.setBizMode(MessageMode.Publishing.getValue());
        message.setSendTime(Calendar.getInstance().getTime());
        this.save(message);
        List<MessageReceive> messageReceives = Lists.newArrayList();
        for (String objectId : receiveObjectIds) {
            MessageSender messageSender = new MessageSender(message.getId());
            messageSender.setObjectType(messageReceiveObjectType.getValue());
            messageSender.setObjectId(objectId);
            messageSenderService.save(messageSender);

            List<String> targetIds = Lists.newArrayList();
            if (MessageReceiveObjectType.User.equals(messageReceiveObjectType)) {
                targetIds.add(objectId);
            } else if (MessageReceiveObjectType.Organ.equals(messageReceiveObjectType)) {
                targetIds = userService.findUsersLoginNamesByOrganId(objectId);
            } else if (MessageReceiveObjectType.Member.equals(messageReceiveObjectType)) {
                targetIds.add(objectId);

            }
            for (String targetId : targetIds) {
                MessageReceive messageReceive = new MessageReceive(message.getId());
                messageReceive.setUserId(targetId);
                messageReceive.setIsRead(YesOrNo.NO.getValue());
                messageReceiveService.save(messageReceive);
                messageReceives.add(messageReceive);
            }
        }
        message.setBizMode(MessageMode.Published.getValue());
        this.save(message);
        message.setMessageReceives(messageReceives);
        return message;
    }
}
