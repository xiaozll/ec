package com.eryansky;

import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.modules.sys.dao.VersionLogDao;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.ConfigService;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.modules.sys.service.VersionLogService;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.eryansky.modules.sys.mapper.VersionLogDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.select.SelectDSL.select;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private ConfigService configService;
	@Autowired
	private UserService userService;
	@Autowired
	private VersionLogService versionLogService;

	@Test
	public void contextLoads() {
		userService.aop();
	}


	@Test
	public void e() {
		System.out.println(Encrypt.e("1"));
	}


	@Test
	public void generateSerialNumberByModelCode() {
		System.out.println(SystemSerialNumberUtils.generateSerialNumberByModelCode("A01"));
	}


	@Test
	public void testSelectByExample() {
		SelectStatementProvider selectStatement = select(data.allColumns())
				.from(data)
				.where(app,isEqualTo("1"))
				.build()
				.render(RenderingStrategies.MYBATIS3);

		List<VersionLog> rows = versionLogService.selectMany(selectStatement);
		System.out.println(JsonMapper.toJsonString(rows));
	}
}
