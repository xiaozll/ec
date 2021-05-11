package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.JobDetails;

import java.util.List;

@MyBatisDao
public interface JobDao extends CrudDao<JobDetails> {
	public List<JobDetails> getJobList(Parameter parameter);
}
