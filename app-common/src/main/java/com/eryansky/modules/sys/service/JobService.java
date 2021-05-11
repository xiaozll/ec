package com.eryansky.modules.sys.service;


import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.JobDao;
import com.eryansky.modules.sys.mapper.JobDetails;
import com.eryansky.utils.AppConstants;
import org.springframework.stereotype.Service;


@Service
public class JobService extends CrudService<JobDao, JobDetails> {
	
	public Page<JobDetails> getJobList(Page<JobDetails> page, String jobName, String jobState) {
		Parameter parameter = Parameter.newPageParameter(page, AppConstants.getJdbcType());
		parameter.put("jobName",jobName);
		parameter.put("jobState",jobState);
		page.setResult(dao.getJobList(parameter));
		return page;
	}

}