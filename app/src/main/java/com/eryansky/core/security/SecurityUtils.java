/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.net.IpUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.modules.sys.mapper.*;
import com.eryansky.modules.sys.service.PostService;
import com.eryansky.modules.sys.service.ResourceService;
import com.eryansky.modules.sys.service.RoleService;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.core.security._enum.DeviceType;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统使用的特殊工具类 简化代码编写.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-18 上午8:25:36
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    public static final class Static {
        private static ResourceService resourceService = SpringContextHolder.getBean(ResourceService.class);
        private static UserService userService = SpringContextHolder.getBean(UserService.class);
        private static RoleService roleService = SpringContextHolder.getBean(RoleService.class);
        private static PostService postService = SpringContextHolder.getBean(PostService.class);
        private static ApplicationSessionContext applicationSessionContext = ApplicationSessionContext.getInstance();
    }

    /**
     * 是否授权某个资源
     *
     * @param resourceCode 资源编码
     * @return
     */
    public static Boolean isPermitted(String resourceCode) {
        return isPermitted(null,resourceCode);
    }


    /**
     * 是否授权某个资源
     *
     * @param userId 用户ID
     * @param resourceCode 资源编码
     * @return
     */
    public static Boolean isPermitted(String userId,String resourceCode) {
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (userId == null) {
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                logger.warn("用户不存在.");
                return false;
            }

//            flag = resourceService.isUserPermittedResourceCode(sessionInfo.getUserId(), resourceCode);
            if(sessionInfo != null && userId.equals(sessionInfo.getUserId())){
                if (sessionInfo.isSuperUser()) {// 超级用户
                    return true;
                }
                return null != sessionInfo.getPermissons().stream().filter(permisson -> resourceCode.equalsIgnoreCase(permisson.getCode())).findFirst().orElse(null);
            }else{
                return Static.resourceService.isPermittedResourceCodeWithPermission(userId,resourceCode);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return false;
    }

    /**
     * 是否授权某个URL地址
     *
     * @param url 资源编码
     * @return
     */
    public static Boolean isPermittedUrl(String url) {
        return isPermittedUrl(null,url);
    }


    /**
     * 是否授权某个URL地址
     *
     * @param userId 用户ID
     * @param url 资源编码
     * @return
     */
    public static Boolean isPermittedUrl(String userId, String url) {
        boolean flag = false;
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (userId == null) {
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                logger.warn("用户不存在.");
                return false;
            }

            //是否需要拦截
//            boolean needInterceptor = resourceService.isInterceptorUrl(url);
//            if(!needInterceptor){
//                return true;
//            }

            if(sessionInfo != null && userId.equals(sessionInfo.getUserId())){
                if (sessionInfo.isSuperUser()) {// 超级用户
                    return true;
                }

                for(Permisson permisson:sessionInfo.getPermissons()){
                    if(!flag && StringUtils.isNotBlank(permisson.getMarkUrl())){
                        String[] markUrls = permisson.getMarkUrl().split(";");
                        for(int i=0;i<markUrls.length;i++){
                            if(StringUtils.isNotBlank(markUrls[i]) && StringUtils.simpleWildcardMatch(markUrls[i],url)){
                                flag = true;
                                break;
                            }
                        }
                    }
                }
                return flag;
            }else{
                return Static.resourceService.isPermittedResourceMarkUrlWithPermissions(userId,url);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return flag;
    }



    /**
     * 是否授权某个角色
     *
     * @param roleCode 角色编码
     * @return
     */
    public static Boolean isPermittedRole(String roleCode) {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return isPermittedRole(sessionInfo.getUserId(), roleCode);
    }

    /**
     * 判断某个用户是否授权某个角色
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return
     */
    public static Boolean isPermittedRole(String userId, String roleCode) {
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (userId == null) {
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                throw new SystemException("用户[" + userId + "]不存在.");
            }

            if(sessionInfo != null && userId.equals(sessionInfo.getUserId())){
                if (sessionInfo.isSuperUser()) {// 超级用户
                    return true;
                }
                return null != sessionInfo.getPermissonRoles().stream().filter(permissonRole -> roleCode.equalsIgnoreCase(permissonRole.getCode())).findFirst().orElse(null);
            }else{
                List<Role> list = Static.roleService.findRolesByUserId(userId);
                return null != list.stream().filter(role -> StringUtils.isNotBlank(role.getCode()) && roleCode.equalsIgnoreCase(role.getCode())).findFirst().orElse(null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    /**
     * 获取当前用户最大的数据权限范围
     * @return
     */
    public static String getUserMaxRoleDataScope(){
        return isCurrentUserAdmin() ? DataScope.ALL.getValue() : getUserMaxRoleDataScope(getCurrentUserId());
    }

    /**
     * 判断当前用户是否授权所有数据
     * @return
     */
    public static boolean isPermittedMaxRoleDataScope(){
        return isPermittedMaxRoleDataScope(getCurrentUserId());
    }

    /**
     * 判断用户是否授权所有数据
     * @return
     */
    public static boolean isPermittedMaxRoleDataScope(String userId){
        return isCurrentUserAdmin() || DataScope.ALL.getValue().equals(getUserMaxRoleDataScope(userId));
    }

    /**
     * 获取用户最大的数据权限范围
     * @param userId
     * @return
     */
    public static String getUserMaxRoleDataScope(String userId){
        User user = UserUtils.getUser(userId);
        // 获取到最大的数据权限范围
        int dataScopeInteger = Integer.valueOf(DataScope.SELF.getValue());
        List<Role> roles = Static.roleService.findRolesByUserId(user.getId());
        for (Role r : roles) {
            if(StringUtils.isBlank(r.getDataScope())){
                continue;
            }
            int ds = Integer.valueOf(r.getDataScope());
            if (ds == Integer.valueOf(DataScope.CUSTOM.getValue())) {
                dataScopeInteger = ds;
                break;
            } else if (ds < dataScopeInteger) {
                dataScopeInteger = ds;
            }
        }
        return String.valueOf(dataScopeInteger);
    }

    /**
     * 判断当前用户是否有某个岗位
     *
     * @param postCode 角色编码
     * @return
     */
    public static Boolean hasPost(String postCode) {
        return hasPost(null,postCode);
    }

    /**
     * 判断某个用户是否有某个刚问
     *
     * @param userId   用户ID
     * @param postCode 角色编码
     * @return
     */
    public static Boolean hasPost(String userId, String postCode) {
        boolean flag = false;
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (userId == null) {
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                throw new SystemException("用户[" + userId + "]不存在.");
            }

            if(sessionInfo != null && userId.equals(sessionInfo.getUserId())){
                return null != sessionInfo.getPostCodes().stream().filter(postCode::equals).findFirst().orElse(null);
            }

            List<Post> posts = Static.postService.findPostsByUserId(userId);
            return null != posts.stream().filter(post -> postCode.equals(post.getCode())).findFirst().orElse(null);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * User转SessionInfo.
     *
     * @param user
     * @return
     */
    private static SessionInfo userToSessionInfo(User user) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(user.getId());
        sessionInfo.setName(user.getName());
        sessionInfo.setLoginName(user.getLoginName());
        List<String> roleIds = Static.roleService.findRoleIdsByUserId(user.getId());
        sessionInfo.setRoleIds(roleIds);
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(user.getId());
        sessionInfo.setLoginOrganId(organExtend.getId());
        sessionInfo.setLoginOrganSysCode(organExtend.getSysCode());
        sessionInfo.setLoginOrganName(organExtend.getName());
        sessionInfo.setLoginCompanyId(organExtend.getCompanyId());
        sessionInfo.setLoginCompanyCode(organExtend.getCompanyCode());
        return sessionInfo;
    }


    /**
     * 初始化权限
     * @param sessionInfo
     * @return
     */
    private static void initPermission(SessionInfo sessionInfo){
        List<Resource> resources = Static.resourceService.findAuthorityResourcesByUserId(sessionInfo.getUserId());
        resources.forEach(resource->{
            sessionInfo.addPermissons(new Permisson(StringUtils.isNotBlank(resource.getCode()) ? resource.getCode():resource.getId(),resource.getMarkUrl()));
        });
        List<Role> roles = Static.roleService.findRolesByUserId(sessionInfo.getUserId());
        roles.forEach(role -> {
            sessionInfo.addPermissonRoles(new PermissonRole(StringUtils.isNotBlank(role.getCode()) ? role.getCode():role.getId()));
        });

        List<Post> posts = Static.postService.findPostsByUserId(sessionInfo.getUserId());
        posts.forEach(post -> {
            sessionInfo.getPostCodes().add(StringUtils.isNotBlank(post.getCode()) ? post.getCode():post.getId());
        });
    }


    /**
     * 将用户放入session中.
     *
     * @param user
     */
    public static SessionInfo putUserToSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        if(logger.isDebugEnabled()){
            logger.debug("putUserToSession:{}", sessionId);
        }
        SessionInfo sessionInfo = userToSessionInfo(user);
        sessionInfo.setIp(IpUtils.getIpAddr0(request));
        sessionInfo.setUserAgent(UserAgentUtils.getHTTPUserAgent(request));
        sessionInfo.setDeviceType(UserAgentUtils.getDeviceType(request).toString());
        sessionInfo.setBrowserType(UserAgentUtils.getBrowser(request).getName());
        String code = WebUtils.getParameter(request, "code");
        sessionInfo.setDeviceCode(code);
        String longitude_s = WebUtils.getParameter(request, "longitude");
        String latitude_s = WebUtils.getParameter(request, "latitude");
        String accuracy_s = WebUtils.getParameter(request, "accuracy");
        sessionInfo.setLongitude(StringUtils.isBlank(longitude_s) ? null : BigDecimal.valueOf(Double.valueOf(longitude_s)));
        sessionInfo.setLatitude(StringUtils.isBlank(latitude_s) ? null : BigDecimal.valueOf(Double.valueOf(latitude_s)));
        sessionInfo.setAccuracy(StringUtils.isBlank(accuracy_s) ? null : BigDecimal.valueOf(Double.valueOf(accuracy_s)));
        String appVersion_s = WebUtils.getParameter(request, "appVersion");
        sessionInfo.setAppVersion(appVersion_s);
        sessionInfo.setSessionId(sessionId);
        sessionInfo.setId(SecurityUtils.getNoSuffixSessionId(session));
        try {
            sessionInfo.setHost(IpUtils.toIpString(InetAddress.getLocalHost()));
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }

        String userAgent = UserAgentUtils.getHTTPUserAgent(request);
        boolean likeIOS = AppUtils.likeIOS(userAgent);
        boolean likeAndroid = AppUtils.likeAndroid(userAgent);
        if(likeIOS){
            sessionInfo.setSysTemDeviceType(DeviceType.iPhone.getDescription());
        }else if(likeAndroid){
            sessionInfo.setSysTemDeviceType(DeviceType.Android.getDescription());
        }else{
            sessionInfo.setSysTemDeviceType(DeviceType.PC.getDescription());
        }

        initPermission(sessionInfo);

        Static.applicationSessionContext.addSession(sessionInfo);
        request.getSession().setAttribute("loginUser",sessionInfo.getName()+"["+sessionInfo.getLoginName()+"]");
        return sessionInfo;
    }


    /**
     * 将用户放入session中. 测试用
     *
     * @param sessionId
     * @param user
     */
    public static SessionInfo putUserToSession(String sessionId,User user) {
        if(logger.isDebugEnabled()){
            logger.debug("putUserToSession:{}", sessionId);
        }
        SessionInfo sessionInfo = userToSessionInfo(user);
        sessionInfo.setSessionId(sessionId);
        sessionInfo.setId(sessionId);

        sessionInfo.setSysTemDeviceType(DeviceType.PC.getDescription());

        initPermission(sessionInfo);

        Static.applicationSessionContext.addSession(sessionInfo);
        return sessionInfo;
    }

    /**
     * 重新加载当前登录用户权限
     * @return
     */
    public static SessionInfo reloadCurrentSessionPermission(){
        SessionInfo sessionInfo = getCurrentSessionInfo();
        if(null == sessionInfo){
            return null;
        }
        sessionInfo.getPermissons().clear();
        sessionInfo.getPermissonRoles().clear();
        sessionInfo.getPostCodes().clear();
        initPermission(sessionInfo);
        Static.applicationSessionContext.addSession(sessionInfo);
        return sessionInfo;
    }

    /**
     * 重新加载用户权限
     * @param userId 用户ID
     * @return
     */
    public static void reloadSessionPermission(String userId){
        List<SessionInfo> sessionInfos = findSessionInfoByUserId(userId);
        sessionInfos.forEach(sessionInfo -> {
            sessionInfo.getPermissons().clear();
            sessionInfo.getPermissonRoles().clear();
            sessionInfo.getPostCodes().clear();
            initPermission(sessionInfo);
            Static.applicationSessionContext.addSession(sessionInfo);
        });
    }

    /**
     * 获取当前用户session信息.
     */
    public static SessionInfo getCurrentSessionInfo() {
        SessionInfo sessionInfo = null;
        try {
            HttpSession session = SpringMVCHolder.getSession();
            sessionInfo = getSessionInfo(SecurityUtils.getNoSuffixSessionId(session),session.getId());
            if(sessionInfo == null){
                String token = SpringMVCHolder.getRequest().getHeader("Authorization");
                if(StringUtils.isNotBlank(token)){
                    sessionInfo = getSessionInfoByToken(StringUtils.replaceOnce(token,"Bearer ",""));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            if(null != sessionInfo){
                sessionInfo.setUpdateTime(Calendar.getInstance().getTime());
                Static.applicationSessionContext.addSession(sessionInfo);
            }
        }

        return sessionInfo;
    }

    /**
     * 获取当前用户session信息.
     */
    public static SessionInfo getCurrentSessionInfo(HttpServletRequest request) {
        SessionInfo sessionInfo = null;
        try {
            sessionInfo = getSessionInfo(SecurityUtils.getNoSuffixSessionId(request.getSession()),request.getSession().getId());
            if(sessionInfo == null){
                String token = SpringMVCHolder.getRequest().getHeader("Authorization");
                if(StringUtils.isNotBlank(token)){
                    sessionInfo = getSessionInfoByToken(StringUtils.replaceOnce(token,"Bearer ",""));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            if(null != sessionInfo){
                sessionInfo.setUpdateTime(Calendar.getInstance().getTime());
                Static.applicationSessionContext.addSession(sessionInfo);
            }
        }
        return sessionInfo;
    }

    /**
     * 获取当前登录用户信息.
     */
    public static User getCurrentUser() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return null == sessionInfo ? null:Static.userService.get(sessionInfo.getUserId());
    }

    /**
     * 获取当前登录用户信息.
     */
    public static String getCurrentUserId() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return null == sessionInfo ? null:sessionInfo.getUserId();
    }

    /**
     * 获取当前登录用户账号信息.
     */
    public static String getCurrentUserLoginName() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return null == sessionInfo ? null:sessionInfo.getLoginName();
    }

    /**
     * 获取当前登录用户名称信息.
     */
    public static String getCurrentUserName() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return null == sessionInfo ? null:sessionInfo.getName();
    }

    /**
     * 判断当前用户登录用户 是否是超级管理员
     * @return
     */
    public static boolean isCurrentUserAdmin() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return isUserAdmin(sessionInfo.getUserId());
    }


    /**
     * 判断是否是超级管理员
     * @param userId 用户ID
     * @return
     */
    public static boolean isUserAdmin(String userId) {
        User superUser = Static.userService.getSuperUser();
        return userId != null && superUser != null && userId.equals(superUser.getId());
    }

    /**
     * 根据用户ID获取用户对象
     * @param userId
     * @return
     */
    public static User getUserById(String userId) {
        return UserUtils.getUser(userId);
    }

    /**
     * 用户下线
     * @param sessionId sessionID
     */
    public static void offLine(String sessionId){
        removeSessionInfoFromSession(sessionId, SecurityType.offline);
    }

    /**
     * 用户下线
     * @param sessionIds sessionID集合
     */
    public static void offLine(List<String> sessionIds){
        if(Collections3.isNotEmpty(sessionIds)){
            sessionIds.forEach(sessionId -> removeSessionInfoFromSession(sessionId, SecurityType.offline));
        }
    }

    /**
     * 全部下线
     */
    public static void offLineAll(){
        List<SessionInfo> sessionInfos = SecurityUtils.findSessionInfoListWithOrder();
        sessionInfos.forEach(sessionInfo -> removeSessionInfoFromSession(sessionInfo.getId(), SecurityType.offline));
    }

    /**
     * 将用户信息从session中移除
     *
     * @param sessionId session ID
     */
    public static void removeSessionInfoFromSession(String sessionId, SecurityType securityType) {
        SessionInfo _sessionInfo = Static.applicationSessionContext.getSession(sessionId);
        if(_sessionInfo != null){
            Static.userService.logout(_sessionInfo.getUserId(),securityType);
        }
        Static.applicationSessionContext.removeSession(sessionId);
        try {
            HttpSession httpSession = SpringMVCHolder.getSession();
            if(httpSession != null && SecurityUtils.getNoSuffixSessionId(httpSession).equals(sessionId)){
                httpSession.invalidate();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * 查看当前登录用户信息 按时间排序（降序）
     * @return
     */
    public static List<SessionInfo> findSessionInfoListWithOrder() {
        List<SessionInfo> sessionInfoData = Static.applicationSessionContext.findSessionInfoData();
        //排序
        sessionInfoData.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return sessionInfoData;
    }

    /**
     * 查看当前登录用户信息
     * @return
     */
    public static List<SessionInfo> findSessionInfoList() {
        return Static.applicationSessionContext.findSessionInfoData();
    }


    /**
     * 查看当前登录用户信息 （分页查询）
     * @param page
     * @return
     */
    public static Page<SessionInfo> findSessionInfoPage(Page<SessionInfo> page) {
        return findSessionInfoPage(page,null);
    }

    /**
     * 查看当前登录用户信息 （分页查询）
     * @param page
     * @param query 查询条件
     * @return
     */
    public static Page<SessionInfo> findSessionInfoPage(Page<SessionInfo> page, String query) {
        List<SessionInfo> list = StringUtils.isNotBlank(query) ? findSessionInfoByQuery(query): findSessionInfoListWithOrder();
        page.setTotalCount(list.size());
        if(Page.PAGESIZE_ALL == page.getPageSize()){
            return page.setResult(list);
        }
        return page.setResult(AppUtils.getPagedList(list,page.getPageNo(),page.getPageSize()));
    }

    /**
     * Session size
     * @return
     */
    public static int getSessionInfoSize() {
        return Static.applicationSessionContext.findSessionInfoKeySize();
    }

    /**
     * Session keys
     * @return
     */
    public static Collection<String> findSessionInfoKeys() {
        return Static.applicationSessionContext.findSessionInfoKeys();
    }



    /**
     * 查看某个用户登录信息
     * @param token
     * @return
     */
    public static SessionInfo getSessionInfoByToken(String token) {
        List<SessionInfo> list = findSessionInfoListWithOrder();
        return list.stream().filter(sessionInfo -> token.equals(sessionInfo.getToken())).findFirst().orElse(null);
    }

    /**
     * 查看某个用户登录信息
     * @param loginName 登录帐号
     * @return
     */
    public static List<SessionInfo> findSessionInfoByLoginName(String loginName) {
        List<SessionInfo> list = findSessionInfoListWithOrder();
        return list.stream().filter(sessionInfo -> loginName.equals(sessionInfo.getLoginName())).collect(Collectors.toList());
    }

    /**
     * 查看某个用户登录信息
     * @param userId 用户ID
     * @return
     */
    public static List<SessionInfo> findSessionInfoByUserId(String userId) {
        List<SessionInfo> list = findSessionInfoList();
        return list.stream().filter(sessionInfo -> userId.equals(sessionInfo.getUserId())).collect(Collectors.toList());
    }

    /**
     * 查看session信息
     *
     * @param query 查询条件
     * @return
     */
    public static List<SessionInfo> findSessionInfoByQuery(String query) {
        if (StringUtils.isBlank(query)){
            return Collections.emptyList();
        }
        List<SessionInfo> list = findSessionInfoListWithOrder();
        return list.stream().filter(sessionInfo -> StringUtils.contains(sessionInfo.getLoginName(), query) || StringUtils.contains(sessionInfo.getName(), query)).collect(Collectors.toList());
    }

    /**
     * 根据SessionId查找对应的SessionInfo信息
     * @param id
     * @return
     */
    public static SessionInfo getSessionInfo(String id) {
        return getSessionInfo(id,null);
    }

    /**
     * 根据SessionId查找对应的SessionInfo信息
     * @param id
     * @param sessionId
     * @return
     */
    public static SessionInfo getSessionInfo(String id, String sessionId) {
        SessionInfo sessionInfo = Static.applicationSessionContext.getSession(id);
        //更新真实的SessionID
        if(sessionInfo != null && sessionId != null && !sessionInfo.getSessionId().equals(sessionId)){
            sessionInfo.setSessionId(sessionId);
            Static.applicationSessionContext.addSession(sessionInfo);
        }
        return sessionInfo;
    }


    public static boolean isMobileLogin(){
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return sessionInfo != null && sessionInfo.isMobileLogin();
    }

    /**
     * 去除jvmRoute后缀
     * @param session
     * @return
     */
    public static String getNoSuffixSessionId(HttpSession session){
        return null == session ? null:StringUtils.substringBefore(session.getId(),".");
    }

}

