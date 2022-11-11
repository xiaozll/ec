/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.NoticeSendInfo;

import java.util.List;

/**
 * @author Eryan
 * @date 2015-10-15
 */
@MyBatisDao
public interface NoticeSendInfoDao extends CrudDao<NoticeSendInfo> {

    int deleteByNoticeId(Parameter parameter);

    List<NoticeSendInfo> findQueryList(NoticeSendInfo entity);

    List<String> findObjectIdsByNoticeId(NoticeSendInfo entity);
}
