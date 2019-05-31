package com.eryansky;

import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.utils.CacheUtils;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTests {

	@Autowired(required = false)
	private RedisTemplate<String, Serializable> redisTemplate;

	@Test
	public void contextLoads() {
	}


	@Test
	public void cache() {
		Long d1  = System.currentTimeMillis();
		List<String> ids= Lists.newArrayList();
		for(int i=0;i<10000;i++){
			String uuid = Identities.uuid2();
			ids.add(uuid);
			CacheUtils.put(uuid,uuid);
		}
		Long d2  = System.currentTimeMillis();
		System.out.println(d2 - d1);
		for(String id:ids){
			System.out.println((String)CacheUtils.get(id));
		}

		Long d3  = System.currentTimeMillis();
		System.out.println(d3 - d2);
	}

	@Test
	public void redis() {
		Long d1  = System.currentTimeMillis();
		CacheUtils.put("123",21);
		System.out.println(JsonMapper.toJsonString(redisTemplate.keys("sysCache*")));
		Long d2  = System.currentTimeMillis();
		System.out.println(d2 - d1);
	}

}
