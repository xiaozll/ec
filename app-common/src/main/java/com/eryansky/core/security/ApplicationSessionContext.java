package com.eryansky.core.security;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.utils.CacheUtils;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用Session上下文
 */
public class ApplicationSessionContext {

	public final static String CACHE_SESSION = "sessionCache";
	public final static String CACHE_SESSION_SERVLET = "session";

	/**
	 * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
	 */
	public static final class Static {
		public static final ApplicationSessionContext instance = new ApplicationSessionContext();
	}

	private ApplicationSessionContext() {
	}

	public static ApplicationSessionContext getInstance() {
		return Static.instance;
	}

	public void addSession(SessionInfo sessionInfo) {
		addSession(null,sessionInfo);
	}

	public void addSession(String sessionId,SessionInfo sessionInfo) {
		if (sessionInfo != null) {
			CacheUtils.put(CACHE_SESSION, StringUtils.isNotBlank(sessionId) ? sessionId:sessionInfo.getId(),sessionInfo);
		}
	}

	public void removeSession(String sessionId) {
		if (sessionId != null) {
			CacheUtils.remove(CACHE_SESSION, sessionId);
		}
	}

	public SessionInfo getSession(String sessionId) {
		if (sessionId == null) return null;
		return CacheUtils.get(CACHE_SESSION,sessionId);
	}

	public List<SessionInfo> findSessionInfoDataRemoveDuplicate() {
		List<SessionInfo> list = findSessionInfoData();
		return list.parallelStream().collect(Collectors.toSet()).parallelStream().collect(Collectors.toList());
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
		return CacheUtils.get(cacheName, key);
	}

	public List<Object> findSessionData(String cacheName) {
		Collection<String> keys = CacheUtils.keys(cacheName);
		return CacheUtils.get(CACHE_SESSION,keys);
	}


//	public void addServletSession(String sessionId, HttpSession session) {
//		CacheUtils.put(CACHE_SESSION_SERVLET, sessionId,session);
//	}
//
//	public HttpSession getServletSession(String sessionId) {
//		if (sessionId == null) return null;
//		return CacheUtils.get(CACHE_SESSION_SERVLET,sessionId);
//	}
//
//	public void removeServletSession(String sessionId) {
//		if (sessionId != null) {
//			CacheUtils.remove(CACHE_SESSION_SERVLET, sessionId);
//		}
//	}



}