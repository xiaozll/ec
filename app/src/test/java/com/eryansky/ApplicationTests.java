package com.eryansky;

import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.modules.sys.dao.VersionLogDao;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.*;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;
import com.eryansky.modules.sys.utils.UserUtils;
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
	private RoleService roleService;
	@Autowired
	private PostService postService;
	@Autowired
	private VersionLogService versionLogService;

	@Test
	public void contextLoads() {
		userService.aop();
	}


	@Test
	public void addUserRoleAndPost() {
		String userId = "1eb60440083945bb84394b7b1cc77414";
		String postCode = "post1";
		String roleCode = "role1";
		System.out.println(JsonMapper.toJsonString(postService.findPostsByUserId(userId)));
		UserUtils.addUserOrganPost(userId,postCode);
		System.out.println(JsonMapper.toJsonString(postService.findPostsByUserId(userId)));

		System.out.println(JsonMapper.toJsonString(roleService.findRolesByUserId(userId)));
		UserUtils.addUserRole(userId,roleCode);
		System.out.println(JsonMapper.toJsonString(roleService.findRolesByUserId(userId)));
	}


	@Test
	public void e() {
		System.out.println(Encrypt.e("1"));
	}


	@Test
	public void generateSerialNumberByModelCode() {
		for(int i=1;i<5000;i++){
			int finalI = i;
			new Thread(() ->{
				System.out.println(Thread.currentThread().getName() +" "+ finalI +" " +SystemSerialNumberUtils.generateSerialNumberByModelCode("A01"));
			}).start();
		}

		ThreadUtils.sleep(30*1000);

	}


	@Test
	public void testSelectByExample() {
		SelectStatementProvider selectStatement = select(data.allColumns())
				.from(data,"a")
				.where(app,isEqualTo("1"))
				.build()
				.render(RenderingStrategies.MYBATIS3);

		List<VersionLog> rows = versionLogService.selectMany(selectStatement);
		System.out.println(JsonMapper.toJsonString(rows));
	}
}
