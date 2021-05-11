package com.eryansky.modules.sys._enum;

/**
 *  任务状态
 */
public enum JobState {

	WAITING("WAITING", "等待"),
	PAUSED("PAUSED", "暂停"),
	ACQUIRED("ACQUIRED", "正常执行"),
	BLOCKED("BLOCKED", "阻塞"),
	ERROR("ERROR", "错误");
	/**
	 * 值 String型
	 */
	private final String value;

	/**
	 * 描述 String型
	 */
	private final String description;

	JobState(String value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * 获取值
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 获取描述信息
	 * @return description
	 */
	public String getDescription() {
		return description;
	}


	public static JobState getByValue(String value) {
		if (null == value) {
			return null;
		}
		for (JobState _enum : JobState.values()) {
			if (value.equals(_enum.getValue())) {
				return _enum;
			}
		}
		return null;
	}

	public static JobState getByDescription(String description) {
		if (null == description) {
			return null;
		}
		for (JobState _enum : JobState.values()) {
			if (description.equals(_enum.getDescription())) {
				return _enum;
			}
		}
		return null;
	}

}