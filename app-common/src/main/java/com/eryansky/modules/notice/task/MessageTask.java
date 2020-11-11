/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.task;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.notice.service.MessageReceiveService;
import com.eryansky.modules.notice.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息发送异步任务
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
@Component
public class MessageTask {

    private static Logger logger = LoggerFactory.getLogger(MessageTask.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageReceiveService messageReceiveService;

    /**
     * 发送消息
     *
     * @param message
     * @param receiveObjectType
     */
    @Async
    public void saveAndSend(Message message, String receiveObjectType, List<String> receiveObjectIds) {
        MessageReceiveObjectType messageReceiveObjectType = MessageReceiveObjectType.getByValue(receiveObjectType);
        if(null == messageReceiveObjectType){
            logger.warn("参数[receiveObjectType]未定义对应类型，{}",receiveObjectType);
            return;
        }
        if (MessageReceiveObjectType.User.equals(messageReceiveObjectType)) {
            saveAndSend(message, messageReceiveObjectType, receiveObjectIds);
        } else if (MessageReceiveObjectType.Organ.equals(messageReceiveObjectType)) {
            saveAndSend(message, messageReceiveObjectType, receiveObjectIds);
        }else{
            logger.warn("[receiveObjectType]未实现的类型",receiveObjectType);
        }
    }

    /**
     * @param message
     * @param messageReceiveObjectType
     * @param receiveObjectIds
     */
    @Async
    public void saveAndSend(Message message, MessageReceiveObjectType messageReceiveObjectType, List<String> receiveObjectIds) {
        messageService.saveAndSend(message, messageReceiveObjectType, receiveObjectIds);
    }

    /**
     * 设置通知已读状态
     *
     * @param receive
     */
    @Async
    public void setRead(MessageReceive receive) {
        messageReceiveService.setRead(receive);
    }

    /**
     * 设置通知已读状态
     *
     * @param userId
     */
    @Async
    public void setReadAll(String userId) {
        messageReceiveService.setReadAll(userId, null);
    }

    /**
     * 重新推送用户消息
     *
     * @param userId
     */
    @Async
    public void reSendByUserId(String userId) {
        messageReceiveService.reSendByUserId(userId);
    }
}
