/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.event;

import com.eryansky.common.mail.entity.Mail;

import java.util.List;

/**
 * 可订阅类接口，实现订阅接口的规范
 *
 * @author eryan
 * @date 2015-09-14
 */
@Deprecated
public interface SubscriptionListener {
	/**
	 * 推送电子邮件给订阅者
	 * 
	 * @param mailList 被推送给订阅者的电子邮件列表
	 */
    void onDeliver(List<Mail> mailList);
}
