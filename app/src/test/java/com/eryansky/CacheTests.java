package com.eryansky;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.model.Result;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.j2cache.lock.DefaultLockCallback;
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

	@Test
	public void ttl(){
		CacheUtils.getCacheChannel().set("default","key","1");
		System.out.println(CacheUtils.getCacheChannel().get("default","key"));
		System.out.println(CacheUtils.getCacheChannel().ttl("default","key"));;
	}


	@Test
	public void cache13() throws Exception{
		for(int i=0;i<10;i++){
			int finalI = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					String key = "1";
					p13(key, finalI);
				}
			}).start();
		}
		ThreadUtils.sleep(300*1000);
	}
	private void p13(String key,int index){
		Result result = CacheUtils.getCacheChannel().lock(key, 10, 30, new DefaultLockCallback<Result>(null,null) {
			@Override
			public Result handleObtainLock() {
				try{

					return s13(index);
				}catch (ServiceException e){
					return Result.warnResult().setMsg(e.getMessage());
				}
			}
		});
		System.out.println(index+(result != null ? result.toString():" null"));
	}

	private Result s13(int index){
		System.out.println(index);
		ThreadUtils.sleep(5*1000);
		return Result.successResult();
	}
}
