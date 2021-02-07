package com.eryansky.j2cache.cache.support.redis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.eryansky.j2cache.lock.LockCallback;
import com.eryansky.j2cache.lock.LockCantObtainException;
import com.eryansky.j2cache.lock.LockInsideExecutedException;
import com.eryansky.j2cache.lock.LockRetryFrequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.eryansky.j2cache.Level2Cache;

public class SpringRedisGenericCache implements Level2Cache {

	private final static Logger log = LoggerFactory.getLogger(SpringRedisGenericCache.class);

	private String namespace;

	private String region;

	private RedisTemplate<String, Serializable> redisTemplate;

	public SpringRedisGenericCache(String namespace, String region, RedisTemplate<String, Serializable> redisTemplate) {
		if (region == null || region.isEmpty()) {
			region = "_"; // 缺省region
		}
		this.namespace = namespace;
		this.redisTemplate = redisTemplate;
		this.region = getRegionName(region);
	}

	private String getRegionName(String region) {
		if (namespace != null && !namespace.isEmpty())
			region = namespace + ":" + region;
		return region;
	}

	@Override
	public void clear() {
		Collection<String> keys = keys();
		keys.forEach(k -> {
			redisTemplate.delete(this.region + ":" + k);
		});
	}

	@Override
	public boolean exists(String key) {
		return 	redisTemplate.execute((RedisCallback<Boolean>) redis -> redis.exists(_key(key)));
	}

	@Override
	public void evict(String... keys) {
		for (String k : keys) {
			redisTemplate.execute((RedisCallback<Long>) redis -> redis.del(_key(k)));
		}
	}

	@Override
	public Collection<String> keys() {
		return redisTemplate.keys(this.region + ":*").stream().map(k->k.substring(this.region.length()+1)).collect(Collectors.toSet());
	}

	@Override
	public byte[] getBytes(String key) {
		return redisTemplate.execute((RedisCallback<byte[]>) redis -> {
			return redis.get(_key(key));
		});
	}

	@Override
	public List<byte[]> getBytes(Collection<String> keys) {
		return redisTemplate.execute((RedisCallback<List<byte[]>>) redis -> {
			byte[][] bytes = keys.stream().map(k -> _key(k)).toArray(byte[][]::new);
			return redis.mGet(bytes);
		});
	}

	@Override
	public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds) {
		if (timeToLiveInSeconds <= 0) {
			log.debug(String.format("Invalid timeToLiveInSeconds value : %d , skipped it.", timeToLiveInSeconds));
			setBytes(key, bytes);
		} else {
			redisTemplate.execute((RedisCallback<List<byte[]>>) redis -> {
				redis.setEx(_key(key), (int) timeToLiveInSeconds, bytes);
				return null;
			});
		}
	}

	@Override
	public void setBytes(Map<String, byte[]> bytes, long timeToLiveInSeconds) {
		bytes.forEach((k, v) -> setBytes(k, v, timeToLiveInSeconds));
	}

	@Override
	public void setBytes(String key, byte[] bytes) {
		redisTemplate.execute((RedisCallback<byte[]>) redis -> {
			redis.set(_key(key), bytes);
			return null;
		});
	}

	@Override
	public void setBytes(Map<String, byte[]> bytes) {
		 bytes.forEach(this::setBytes);
	}

	private byte[] _key(String key) {
		byte[] k;
		try {
			k = (this.region + ":" + key).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
			k = (this.region + ":" + key).getBytes();
		}
		return k;
	}

	@Override
	public Long ttl(String key) {
		return redisTemplate.execute((RedisCallback<Long>) redis -> redis.ttl(_key(key), TimeUnit.SECONDS));
	}

	@Override
	public void queuePush(String... values) {
		for(String value:values){
			redisTemplate.execute((RedisCallback<Long>) redis -> redis.rPush(region.getBytes(), value.getBytes()));
		}
	}

	@Override
	public String queuePop() {
		byte[] result = redisTemplate.execute((RedisCallback<byte[]>) redis -> redis.lPop(region.getBytes()));
		return null == result ? null : new String(result);
	}

	@Override
	public int queueSize() {
		Long result = redisTemplate.execute((RedisCallback<Long>) redis -> redis.lLen(region.getBytes()));
		return null != result ? result.intValue():0;
	}

	@Override
	public Collection<String> queueList() {
		Long length = redisTemplate.execute((RedisCallback<Long>) redis -> redis.lLen(region.getBytes()));
		if(null == length || length == 0){
			return Collections.emptyList();
		}
		List<byte[]> result = redisTemplate.execute((RedisCallback<List<byte[]>>) redis -> redis.lRange(region.getBytes(),0,length-1));

		return null == result ? Collections.emptyList() : result.stream().map(String::new).collect(Collectors.toList());
	}

	@Override
	public void queueClear() {
		clear();
	}

	@Override
	public <T> T lock(LockRetryFrequency frequency, int timeoutInSecond, long keyExpireSeconds, LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
		int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetryInterval()).intValue();
		long now = System.currentTimeMillis();
		for (int i = 0; i < retryCount; i++) {
			Boolean flag = redisTemplate.opsForValue().setIfAbsent(region,String.valueOf(now).getBytes(),keyExpireSeconds, TimeUnit.SECONDS);// 对应setnx命令
			// 判断锁超时 - 防止原来的操作异常，没有运行解锁操作  防止死锁
			if((null == flag || !flag) &&  keyExpireSeconds > 0) {
				Long expire = redisTemplate.getExpire(region, TimeUnit.SECONDS);
				if(null != expire  && expire < 0) {
					// 对应getset，如果key存在返回当前key的值，并重新设置新的值 redis是单线程处理，即使并发存在，这里的getAndSet也是单个执行
					byte[] currentValue = (byte[])redisTemplate.opsForValue().get(region);
					byte[] oldValue = (byte[])redisTemplate.opsForValue().getAndSet(region,String.valueOf(now).getBytes());
					flag = (null != currentValue && null != oldValue && new String(oldValue).equals(new String(currentValue)));
				}
			}
			if(null != flag && flag) {
				try {
					return lockCallback.handleObtainLock();
				} catch (Exception e) {
					log.error(e.getMessage(),e);
					LockInsideExecutedException ie = new LockInsideExecutedException(e);
					return lockCallback.handleException(ie);
				} finally {
					redisTemplate.opsForValue().getOperations().delete(region);// 删除key
				}
			} else {
				try {
					Thread.sleep(frequency.getRetryInterval());
				} catch (InterruptedException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		return lockCallback.handleNotObtainLock();
	}
}
