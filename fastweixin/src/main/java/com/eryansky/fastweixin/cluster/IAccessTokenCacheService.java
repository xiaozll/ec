package com.eryansky.fastweixin.cluster;

/**
 * AccessToken 刷新接口
 * @author eryan
 * @date 2018-10-31
 */
public interface IAccessTokenCacheService {

    AccessTokenCache getAccessTokenCache();

    void putAccessTokenCache(AccessTokenCache accessTokenCache);
}
