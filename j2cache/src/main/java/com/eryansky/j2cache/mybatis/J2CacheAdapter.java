package com.eryansky.j2cache.mybatis;

import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.J2Cache;
import com.eryansky.j2cache.util.Encrypt;
import org.apache.ibatis.cache.Cache;

import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 实现了 MyBatis 的缓存接口
 * @author Eryan
 * @date 2018-07-24
 */
public class J2CacheAdapter implements Cache {

    private static final String DEFAULT_REGION = "default";

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private String id;
    private boolean encodeKey;
    private boolean localCache;

    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    private static final class Static {
        private static CacheChannel cache = getChannel();

        /**
         * CacheChannel
         * @return
         */
        private static CacheChannel getChannel(){
            return J2Cache.getChannel();
        }

        /**
         * 重新加载
         */
        private static void reload(){
            if(null == cache){
                cache = getChannel();
            }
        }
    }

    public J2CacheAdapter(String id) {
        if (id == null)
            id = DEFAULT_REGION;
        this.id = id;
    }

    public void setId(String id) {
        if (id == null)
            id = DEFAULT_REGION;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        if(null == Static.cache){
            Static.reload();
            return;
        }
        String mKey = encodeKey ? Encrypt.md5(key.toString()):key.toString();
        Static.cache.set(this.id, mKey, value, localCache);
    }

    @Override
    public Object getObject(Object key) {
        if(null == Static.cache){
            Static.reload();
            return null;
        }
        String mKey = encodeKey ? Encrypt.md5(key.toString()):key.toString();
        return Static.cache.get(this.id, mKey).getValue();
    }

    @Override
    public Object removeObject(Object key) {
        if(null == Static.cache){
            Static.reload();
            return null;
        }
        String mKey = encodeKey ? Encrypt.md5(key.toString()):key.toString();
        Object obj = Static.cache.get(this.id, mKey).getValue();
        if (obj != null)
            Static.cache.evict(this.id, mKey);
        return obj;
    }

    @Override
    public void clear() {
        if(null == Static.cache){
            Static.reload();
            return;
        }
        Static.cache.clear(this.getId());
    }

    @Override
    public int getSize() {
        if(null == Static.cache){
            Static.reload();
            return 0;
        }
        Collection<String> keys = Static.cache.keys(this.getId());
        return keys != null ? keys.size() : 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    public Boolean getEncodeKey() {
        return encodeKey;
    }

    public void setEncodeKey(Boolean encodeKey) {
        this.encodeKey = encodeKey;
    }

    public boolean isLocalCache() {
        return localCache;
    }

    public void setLocalCache(boolean localCache) {
        this.localCache = localCache;
    }
}