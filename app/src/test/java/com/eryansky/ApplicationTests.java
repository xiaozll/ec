package com.eryansky;

import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.modules.sys.service.ConfigService;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private ConfigService configService;
	@Autowired
	private UserService userService;

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
}
