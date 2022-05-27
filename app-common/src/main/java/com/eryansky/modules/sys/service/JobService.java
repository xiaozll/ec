package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.core.orm.mybatis.service.BaseService;
import com.eryansky.modules.sys.dao.JobDao;
import com.eryansky.modules.sys.mapper.QuartzJobDetail;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService extends BaseService {

    @Autowired
    private JobDao dao;

    public Page<QuartzJobDetail> findJobList(Page<QuartzJobDetail> page, String jobName, String jobState) {
        Parameter parameter = Parameter.newPageParameter(page, AppConstants.getJdbcType());
        parameter.put("jobName", jobName);
        parameter.put("jobState", jobState);
        page.setResult(dao.findJobList(parameter));
        return page;
    }


    public int updateTriggersInstanceName(String schedName, String triggerName, String triggerGroup, String instanceName) {
        Parameter parameter = Parameter.newParameter();
        parameter.put("schedName", schedName);
        parameter.put("triggerName", triggerName);
        parameter.put("triggerGroup", triggerGroup);
        parameter.put("instanceName", instanceName);
        return dao.updateTriggersInstanceName(parameter);
    }

}