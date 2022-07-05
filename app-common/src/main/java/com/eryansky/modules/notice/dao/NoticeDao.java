/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.Notice;

import java.util.List;
import java.util.Map;

/**
 * @author eryan
 * @date 2015-10-15
 */
@MyBatisDao
public interface NoticeDao extends CrudDao<Notice> {

    List<Notice> findQueryList(Map<String, Object> parameter);

    /**
     * 通知附件
     *
     * @param noticeId
     * @return
     */
    List<String> findFileIdsByNoticeId(String noticeId);


    int deleteNoticeFiles(Parameter parameter);

    int insertNoticeFiles(Parameter parameter);
}
