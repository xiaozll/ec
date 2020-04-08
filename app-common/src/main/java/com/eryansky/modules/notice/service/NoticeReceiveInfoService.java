/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice._enum.NoticeReadMode;
import com.eryansky.modules.notice.dao.NoticeReceiveInfoDao;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-15
 */
@Service
public class NoticeReceiveInfoService extends CrudService<NoticeReceiveInfoDao, NoticeReceiveInfo> {

    public NoticeReceiveInfo getUserNotice(String userId, String noticeId) {
        NoticeReceiveInfo receiveInfo = new NoticeReceiveInfo();
        receiveInfo.setUserId(userId);
        receiveInfo.setNoticeId(noticeId);
        return dao.getUserNotice(receiveInfo);
    }

    /**
     * 我的邮件 分页查询.
     *
     * @param page
     * @param userId        用户ID
     * @param noticeQueryVo 查询条件
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public Page<NoticeReceiveInfo> findReadNoticePage(Page<NoticeReceiveInfo> page, NoticeReceiveInfo entity, String userId, NoticeQueryVo noticeQueryVo) throws SystemException,
            ServiceException, DaoException {
        Assert.notNull(userId, "参数[userId]为空!");
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("bizMode", NoticeMode.Effective.getValue());
        if (noticeQueryVo != null && Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())) {
            parameter.put("publishUserId", noticeQueryVo.getPublishUserIds().get(0));
        }
        parameter.put("userId", userId);
        if (noticeQueryVo != null) {
            if (noticeQueryVo.getIsTop() != null) {
                parameter.put("isTop", noticeQueryVo.getIsTop());
            }
            if (noticeQueryVo.getIsRead() != null) {
                parameter.put("isRead", noticeQueryVo.getIsRead());
            }

            if (StringUtils.isNotBlank(noticeQueryVo.getTitle())) {
                parameter.put("title", "%" + noticeQueryVo.getTitle() + "%");
            }
            if (StringUtils.isNotBlank(noticeQueryVo.getContent())) {
                parameter.put("content", "%" + noticeQueryVo.getContent() + "%");
            }
//            if (Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())) {
//                parameter.put("publishUserIds", noticeQueryVo.getPublishUserIds());
//            }

            if (noticeQueryVo.getStartTime() != null) {
                parameter.put("startTime", DateUtils.format(noticeQueryVo.getStartTime(), DateUtils.DATE_TIME_FORMAT));
            }
            if (noticeQueryVo.getEndTime() != null) {
                parameter.put("endTime", DateUtils.format(noticeQueryVo.getEndTime(), DateUtils.DATE_TIME_FORMAT));
            }
        }

        entity.setEntityPage(page);
        parameter.put(BaseInterceptor.PAGE, page);
        parameter.put("dbName", entity.getDbName());
        page.setResult(dao.findQueryList(parameter));

        return page;
    }

    public Page<NoticeReceiveInfo> findUserUnreadNotices(Page<NoticeReceiveInfo> page, String userId) {
        NoticeReceiveInfo noticeReceiveInfo = new NoticeReceiveInfo();
        noticeReceiveInfo.setUserId(userId);
        noticeReceiveInfo.setIsRead(NoticeReadMode.unreaded.getValue());
        Notice notice = new Notice();
        notice.setBizMode(NoticeMode.Effective.getValue());
        noticeReceiveInfo.setNotice(notice);
        noticeReceiveInfo.setEntityPage(page);
        page.setResult(dao.findUserUnreadNotices(noticeReceiveInfo));
        return page;
    }

    /**
     * 设置用户通知为已读状态
     *
     * @param userId    用户ID
     * @return
     */
    public int markUserNoticeReaded(String userId) {
        return updateUserNotices(userId, null, NoticeReadMode.readed.getValue());
    }

    /**
     * 设置用户通知为已读状态
     *
     * @param userId    用户ID
     * @param noticeIds 通知IDS
     * @return
     */
    public int markUserNoticeReaded(String userId, Collection<String> noticeIds) {
        return updateUserNotices(userId, noticeIds, NoticeReadMode.readed.getValue());
    }

    /**
     * 设置用户通知为已读状态
     *
     * @param userId    用户ID
     * @param noticeIds 通知IDS
     * @return
     */
    public int updateUserNotices(String userId, Collection<String> noticeIds, String isRead) {
        Parameter parameter = Parameter.newParameter();
        parameter.put("userId", userId);
        parameter.put("noticeIds", noticeIds);
        parameter.put("isRead", isRead);
        return dao.updateUserNotices(parameter);
    }

    /**
     * 根据通知ID查看
     * @param page
     * @param noticeId
     * @return
     */
    public Page<NoticeReceiveInfo> findNoticeReceiveInfosByNoticeId(Page<NoticeReceiveInfo> page, String noticeId) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("noticeId", noticeId);
        parameter.put(BaseInterceptor.PAGE, page);
        List<NoticeReceiveInfo> list = dao.findQueryList(parameter);
        page.setResult(list);
        return page;
    }

    /**
     * 根据通知ID删除
     *
     * @param noticeId
     * @return
     */
    public int deleteByNoticeId(String noticeId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put("noticeId", noticeId);
        return dao.deleteByNoticeId(parameter);
    }
}
