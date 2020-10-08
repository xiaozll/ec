package com.eryansky;

import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.modules.sys.dao.VersionLogDao;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.*;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

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
		String moduleCode = "A01";
		Map<String,String> params0 = Maps.newHashMap();
		params0.put("customCode1","");
		params0.put("customCode2","");
		String customCode0 = null;
		for(int i=1;i<10;i++){
			int finalI = i;
			new Thread(() ->{
				System.out.println(Thread.currentThread().getName() +" 0-"+ finalI +" " +SystemSerialNumberUtils.generateSerialNumberByModelCode(moduleCode,customCode0,params0));
			}).start();
		}

		Map<String,String> params1 = Maps.newHashMap();
		params1.put("customCode1","A01");
		params1.put("customCode2","A01");
		String customCode1 = "A01";
		for(int i=1;i<10;i++){
			int finalI = i;
			new Thread(() ->{
				System.out.println(Thread.currentThread().getName() +" 1-"+ finalI +" " +SystemSerialNumberUtils.generateSerialNumberByModelCode(moduleCode,customCode1,params1));
			}).start();
		}
		Map<String,String> params2 = Maps.newHashMap();
		params2.put("customCode1","B02");
		params2.put("customCode2","B02");
		String customCode2 = "B02";
		for(int i=1;i<10;i++){
			int finalI = i;
			new Thread(() ->{
				System.out.println(Thread.currentThread().getName() +" 2-"+ finalI +" " +SystemSerialNumberUtils.generateSerialNumberByModelCode(moduleCode,customCode2,params2));
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
