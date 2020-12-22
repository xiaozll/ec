/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.aop;

import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.service.MessageReceiveService;
import com.eryansky.modules.notice.utils.NoticeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息、通知发布切面，由系统参照本示例实现
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-03-04
 */
//@Component
//@Aspect
public class MessageAspect implements InitializingBean, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(MessageAspect.class);

    @Autowired
    private MessageReceiveService messageReceiveService;

    /**
     * 消息发布切面信息推送
     *
     * @param joinPoint 切入点
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.notice.service.MessageService.saveAndSend(..))",returning = "returnObj")
    public void afterMessagePublish(JoinPoint joinPoint, Message returnObj) {
        if (null == returnObj) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        if (null == args || args.length < 3) {
            return;
        }
        MessageReceiveObjectType messageReceiveObjectType = (MessageReceiveObjectType) args[1];
        List<String> receiveObjectIds = (List<String>) args[2];
        if(MessageReceiveObjectType.User.getValue().equals(messageReceiveObjectType.getValue())){

        }
    }


    /**
     * 通知公告 发布切面
     *
     * @param joinPoint 切入点
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.notice.service.NoticeService.publish(..))",returning = "returnObj")
    public void afterNoticePublish(JoinPoint joinPoint, Notice returnObj) {
        if (null == returnObj) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Notice notice = returnObj;
        if (null == args || args.length < 1) {
            return;
        }

        if (null == args || args[0] instanceof String) {
            notice = NoticeUtils.getNotice((String)args[0]);
        }
        List<String> receiveObjectIds = NoticeUtils.findNoticeReceiveUserIds(notice.getId());
    }


    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
