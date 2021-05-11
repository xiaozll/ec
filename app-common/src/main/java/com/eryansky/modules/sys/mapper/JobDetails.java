package com.eryansky.modules.sys.mapper;

import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys._enum.JobState;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class JobDetails extends DataEntity<JobDetails> {
	private String jobName;
	private String jobGroup;
	private String jobClassName;
	private String triggerName;
	private String triggerGroup;
	private String cronExpression;
	private String timeZoneId;
	private Date nextFireTime;
	private Date prevFireTime;
	private String triggerState;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Date getPrevFireTime() {
		return prevFireTime;
	}

	public void setPrevFireTime(Date prevFireTime) {
		this.prevFireTime = prevFireTime;
	}

	public String getTriggerState() {
		return triggerState;
	}

	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
	}

	public String getTriggerStateView(){
		if (StringUtils.isNotBlank(this.triggerState)){
			return JobState.getByValue(this.triggerState).getDescription();
		}
		return null;
	}
}
