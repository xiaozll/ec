/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 消息提醒通道
 */
public enum MessageChannel {

    Message("Message1", "系统提醒"),
    Mail("Mail", "邮件提醒"),
    SMS("SMS", "短信提醒"),
    Weixin("Weixin", "微信提醒"),
    QYWeixin("QYWeixin", "微信提醒"),
    Dingtalk("Dingtalk", "钉钉提醒"),
    APP("APP", "APP提醒");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    MessageChannel(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取描述信息
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static MessageChannel getByValue(String value) {
        if (null == value)
            return null;
        for (MessageChannel _enum : MessageChannel.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static MessageChannel getByDescription(String description) {
        if (null == description)
            return null;
        for (MessageChannel _enum : MessageChannel.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}