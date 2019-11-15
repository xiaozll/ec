package com.eryansky.core.security;

import com.eryansky.utils.CacheUtils;
import java.util.Collection;
import java.util.List;

/**
 * 应用Session上下文
 */
public class ApplicationSessionContext {

	public final static String CACHE_SESSION = "sessionCache";

	/**
	 * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
	 */
	public static final class Static {
		public static ApplicationSessionContext instance = new ApplicationSessionContext();
	}

	private ApplicationSessionContext() {
	}

	public static ApplicationSessionContext getInstance() {
		return Static.instance;
	}

	public void addSession(SessionInfo sessionInfo) {
		if (sessionInfo != null) {
			CacheUtils.put(CACHE_SESSION,sessionInfo.getId(),sessionInfo);
		}
	}

	public void removeSession(String sessionId) {
		if (sessionId != null) {
			CacheUtils.remove(CACHE_SESSION, sessionId);
		}
	}

	public SessionInfo getSession(String sessionId) {
		if (sessionId == null) return null;
		return (SessionInfo) CacheUtils.get(CACHE_SESSION,sessionId);
	}

	public List<SessionInfo> findSessionInfoData() {
		Collection<String> keys = CacheUtils.keys(CACHE_SESSION);
		return findSessionInfoData(keys);
	}

	public List<SessionInfo> findSessionInfoData(Collection<String> keys) {
		return CacheUtils.get(CACHE_SESSION,keys);
	}

	public Collection<String> findSessionInfoKeys() {
		return CacheUtils.keys(CACHE_SESSION);
	}

	public int findSessionInfoKeySize() {
		return CacheUtils.keySize(CACHE_SESSION);
	}


	public void addSession(String cacheName, String key, Object o) {
		if (o != null) {
			CacheUtils.put(cacheName, key, o);
		}
	}

	public void removeSession(String cacheName, String key) {
		if (key != null) {
			CacheUtils.remove(cacheName, key);
		}
	}

	public <T> T getSession(String cacheName, String key) {
		if (key == null) return null;
		return (T) CacheUtils.get(cacheName, key);
	}

	public List<Object> findSessionData(String cacheName) {
		Collection<String> keys = CacheUtils.keys(cacheName);
		return CacheUtils.get(CACHE_SESSION,keys);
	}

}