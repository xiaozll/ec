/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Menu;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.eryansky.modules.sys.mapper.Resource;
import com.eryansky.modules.sys.dao.ResourceDao;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 资源表 service
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@Service
public class ResourceService extends TreeService<ResourceDao, Resource> {

    public Page<Resource> findPage(Page<Resource> page, Resource entity) {
        return page.setResult(dao.findList(entity));
    }

    /**
     * 保存或修改.
     */
    @CacheEvict(value = {CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE}, allEntries = true)
    public void save(Resource entity) {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
        super.save(entity);
    }

    /**
     * 自定义保存资源.
     * <br/>说明：如果保存的资源类型为“功能” 则将所有子资源都设置为“功能”类型
     *
     * @param entity 资源对象
     */
    @CacheEvict(value = {CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE}, allEntries = true)
    public void saveResource(Resource entity) {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
        super.save(entity);
        if (entity.getType() != null && ResourceType.function.getValue().equals(entity.getType())) {
            List<String> types = Lists.newArrayList();
            types.add(entity.getType());
            List<Resource> childs = this.findChilds(entity.getId(), types);
            childs.forEach(subResource->{
                subResource.setType(ResourceType.function.getValue());
                super.save(subResource);
            });
        }
    }


    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = {CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE}, allEntries = true)
    public void deleteByIds(Collection<String> ids) {
        if (Collections3.isNotEmpty(ids)) {
            for (String id : ids) {
                deleteById(id);
            }
        } else {
            logger.warn("参数[ids]为空.");
        }

    }


    @CacheEvict(value = {CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE}, allEntries = true)
    public void deleteById(String id) {
        this.delete(new Resource(id));
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);

    }

    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE},allEntries = true)
    public void deleteOwnerAndChilds(String id){
        dao.deleteOwnerAndChilds(new Resource(id));
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
    }


    /**
     * 根据资源编码获取对象
     *
     * @param resourceCode 资源编码
     * @return
     */
    public Resource getByCode(String resourceCode) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("code", resourceCode);
        return dao.getByCode(parameter);
    }


    /**
     * 查找本机以及下级数据
     *
     * @param id
     * @return
     */
    public List<Resource> findOwnerAndChilds(String id) {
        return this.findOwnerAndChilds(id, null);
    }

    /**
     * 查找本机以及下级数据（下级所有数据）
     *
     * @param id
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findOwnerAndChilds(String id, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put("id", id);
        parameter.put("types", resourceTypes);
        return dao.findOwnAndChilds(parameter);
    }


    /**
     * 查找下级数据 （仅下级数据）
     *
     * @param id 父级ID
     * @return
     */
    public List<Resource> findChilds(String id, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("id", id);
        parameter.put("types", resourceTypes);
        return dao.findChilds(parameter);
    }

    /**
     * 查找下级数据（仅下级数据）
     *
     * @param id
     * @return
     */
    public List<Resource> findChild(String id) {
        return this.findChild(id, null);
    }

    /**
     * 查找下级数据 （仅下级数据）
     *
     * @param id     父级ID
     * @param status 传null则使用默认值 默认值:StatusState.NORMAL.getValue() {@link StatusState}
     * @return
     */
    public List<Resource> findChild(String id, String status) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, StringUtils.isBlank(status) ? DataEntity.STATUS_NORMAL : status);
        parameter.put("parentId", id);
        return dao.findChild(parameter);
    }


    /**
     * 查找用户资源
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findResourcesByUserId(String userId) {
        return findResourcesByUserId(userId, null);
    }

    /**
     * 查找用户资源
     *
     * @param userId        用户ID
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findResourcesByUserId(String userId, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("userId", userId);
        parameter.put("types", resourceTypes);
        return dao.findResourcesByUserId(parameter);
    }

    /**
     * 查找用户资源IDS
     *
     * @param userId 用户ID
     * @return
     */
    public List<String> findResourceIdsByUserId(String userId) {
        return findResourceIdsByUserId(userId, null);
    }

    /**
     * 查找用户资源IDS
     *
     * @param userId        用户ID
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<String> findResourceIdsByUserId(String userId, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("userId", userId);
        parameter.put("types", resourceTypes);
        return dao.findResourceIdsByUserId(parameter);
    }


    /**
     * 查找角色资源
     *
     * @param roleId 角色ID
     * @return
     */
    public List<Resource> findResourcesByRoleId(String roleId) {
        return findResourcesByRoleId(roleId, null);
    }

    /**
     * 查找角色资源
     *
     * @param roleId        角色ID
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findResourcesByRoleId(String roleId, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("roleId", roleId);
        parameter.put("types", resourceTypes);
        return dao.findResourcesByRoleId(parameter);
    }


    /**
     * 查找角色资源IDS
     *
     * @param roleId 角色ID
     * @return
     */
    public List<String> findResourceIdsByRoleId(String roleId) {
        return findResourceIdsByRoleId(roleId, null);
    }

    /**
     * 查找角色资源IDS
     *
     * @param roleId        角色ID
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<String> findResourceIdsByRoleId(String roleId, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("roleId", roleId);
        parameter.put("types", resourceTypes);
        return dao.findResourceIdsByRoleId(parameter);
    }


    /**
     * 查找用户资源
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAuthorityResourcesByUserId(String userId) {
        return findAuthorityResourcesByUserId(userId, null);
    }

    /**
     * 查找用户授权应用、导航菜单资源
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAuthorityAppAndMenuResourcesByUserId(String userId) {
        List<String> resourceTypes = Lists.newArrayList(ResourceType.app.getValue(),ResourceType.menu.getValue());
        return findAuthorityResourcesByUserId(userId, resourceTypes);
    }

    /**
     * 查找用户授权应用菜单资源
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAuthorityAppResourcesByUserId(String userId) {
        List<String> resourceTypes = Lists.newArrayList(ResourceType.app.getValue());
        return findAuthorityResourcesByUserId(userId, resourceTypes);
    }

    /**
     * 查找用户授权导航菜单资源
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAuthorityMenuResourcesByUserId(String userId) {
        List<String> resourceTypes = Lists.newArrayList(ResourceType.menu.getValue());
        return findAuthorityResourcesByUserId(userId, resourceTypes);
    }

    /**
     * 查找授权用户资源
     *
     * @param userId        用户ID
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findAuthorityResourcesByUserId(String userId, Collection<String> resourceTypes) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("userId", userId);
        parameter.put("types", resourceTypes);
        return dao.findAuthorityResourcesByUserId(parameter);
    }

    /**
     * 根据用户ID得到导航栏资源（权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    @Cacheable(value = {CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE})
    public List<TreeNode> findTreeNodeResourcesWithPermissions(String userId) {
        logger.debug("缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE + " 参数：userId=" + userId);
        return resourcesToTreeNode(findResourcesWithPermissions(userId));
    }

    /**
     * 根据用户ID得到导航栏资源（权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findResourcesWithPermissions(String userId) {
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findResources(null);
        } else {
            list = this.findAuthorityResourcesByUserId(userId, null);
        }
        return list;
    }


    /**
     * 查找用户资源(应用、菜单) （权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    @Cacheable(value = {CacheConstants.RESOURCE_USER_MENU_TREE_CACHE})
    public List<TreeNode> findNavTreeNodeWithPermissions(String userId) {
        logger.debug("缓存:{},参数：userId={}", CacheConstants.RESOURCE_USER_MENU_TREE_CACHE ,userId);
        return resourcesToTreeNode(findAppAndMenuWithPermissions(userId));
    }

    /**
     * 查找用户资源(应用、菜单)（权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    public List<Menu> findNavMenuWithPermissions(String userId) {
        return resourcesToMenu(findAppAndMenuWithPermissions(userId));
    }


    /**
     * 查找用户资源(应用、菜单)（权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAppAndMenuWithPermissions(String userId) {
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findAppAndMenuResources();
        } else {
            list = this.findAuthorityAppAndMenuResourcesByUserId(userId);
        }

        return list;
    }

    /**
     * 查找用户资源（应用） （权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAppResourcesWithPermissions(String userId) {
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findAppResources();
        } else {
            list = this.findAuthorityAppResourcesByUserId(userId);
        }
        return list;
    }


    /**
     * 查找用户资源（菜单） （权限控制）
     *
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findMenuResourcesWithPermissions(String userId) {
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findMenuResources();
        } else {
            list = this.findAuthorityMenuResourcesByUserId(userId);
        }

        return list;
    }


    /**
     * 查找菜单资源（应用、菜单）
     *
     * @return
     */
    public List<Resource> findAppAndMenuResources() {
        List<String> resourceTypes = Lists.newArrayList(ResourceType.app.getValue(),ResourceType.menu.getValue());
        return findResources(resourceTypes);
    }

    /**
     * 查找菜单资源(应用)
     *
     * @return
     */
    public List<Resource> findAppResources() {
        List<String> resourceTypes = Lists.newArrayList(ResourceType.app.getValue());
        return findResources(resourceTypes);
    }

    /**
     * 查找菜单资源(菜单)
     *
     * @return
     */
    public List<Resource> findMenuResources() {
        List<String> resourceTypes = Lists.newArrayList(ResourceType.menu.getValue());
        return findResources(resourceTypes);
    }

    /**
     * 查找资源
     *
     * @return
     */
    public List<Resource> findResources() {
        return findResources(null, null);
    }

    /**
     * 查找资源
     *
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findResources(Collection<String> resourceTypes) {
        return findResources(resourceTypes, null);
    }

    /**
     * 查找资源
     *
     * @param resourceTypes     资源类型 为null,则查询所有 {@link ResourceType}
     * @param excludeResourceId 排除的资源ID
     * @return
     */
    public List<Resource> findResources(Collection<String> resourceTypes, String excludeResourceId) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("excludeResourceId", excludeResourceId);
        parameter.put("types", resourceTypes);
        return dao.findQuery(parameter);
    }

    /**
     * 查找所有资源
     *
     * @return
     */
    public List<TreeNode> findTreeNodeResources() {
        List<Resource> list = this.findResources(null);
        return resourcesToTreeNode(list);

    }

    /**
     * 查找所有资源
     *
     * @param excludeResourceId 需要排除的资源ID 子级也会被排除
     * @return
     */
    public List<TreeNode> findTreeNodeResourcesWithExclude(String excludeResourceId) {
        List<Resource> list = this.findResources(null, excludeResourceId);
        return resourcesToTreeNode(list);

    }

    /**
     * 资源集合构造成TreeNode 结构自动调整
     *
     * @param resources
     * @return
     */
    private List<TreeNode> resourcesToTreeNode(Collection<Resource> resources){
        if(Collections3.isEmpty(resources)){
            return Collections.emptyList();
        }
        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        resources.forEach(resource->{
            TreeNode treeNode = this.resourceToTreeNode(resource);
            tempTreeNodes.add(treeNode);
        });
        return AppUtils.toTreeTreeNodes(tempTreeNodes);
    }

    /**
     * @param resources
     * @return
     */
    private List<Menu> resourcesToMenu(Collection<Resource> resources) {
        List<Menu> tempMenus = Lists.newArrayList();
        if (Collections3.isEmpty(resources)) {
            return tempMenus;
        }
        Map<String, Menu> tempMap = Maps.newHashMap();
        resources.forEach(resource->{
            Menu menu = this.resourceToMenu(resource);
            tempMenus.add(menu);
            tempMap.put(resource.getId(), menu);
        });

        Set<String> keyIds = tempMap.keySet();
        Iterator<String> iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()) {
            Menu menu = tempMap.get(iteratorKey.next());
            if (StringUtils.isNotBlank(menu.getpId())) {
                Menu parentMenu = getParentMenu(menu.getpId(), tempMenus);
                if (parentMenu != null) {
                    parentMenu.addChild(menu);
                    iteratorKey.remove();
                }
            }

        }

        List<Menu> result = Lists.newArrayList();
        keyIds = tempMap.keySet();
        iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()) {
            Menu menu = tempMap.get(iteratorKey.next());
            result.add(menu);

        }
        return result;
    }

    /**
     * 查找父级节点
     *
     * @param parentId
     * @param menus
     * @return
     */
    private Menu getParentMenu(String parentId, Collection<Menu> menus) {
        Menu t = null;
        for (Menu menu : menus) {
            if (parentId.equals(menu.getId())) {
                t = menu;
                break;
            }
        }
        return t;
    }

    /**
     * 资源转TreeNode
     *
     * @param resource 资源
     * @return
     */
    private TreeNode resourceToTreeNode(Resource resource) {
        TreeNode treeNode = new TreeNode(resource.getId(), resource.getName(), resource.getIconCls());
        treeNode.setpId(resource.getParentId());
        treeNode.addAttribute("url", resource.getUrl());
        treeNode.addAttribute("markUrl", resource.getMarkUrl());
        treeNode.addAttribute("code", resource.getCode());
        treeNode.addAttribute("type", resource.getType());
        return treeNode;
    }

    /**
     * 资源转Menu
     *
     * @param resource 资源
     * @return
     */
    private Menu resourceToMenu(Resource resource) {
        Assert.notNull(resource, "参数resource不能为空");
        Menu menu = new Menu(resource.getId(), resource.getName());
        menu.setpId(resource.getParentId());
        String url = resource.getUrl();
        menu.setHref(url);
        menu.addAttribute("type", resource.getType());
        return menu;
    }


    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() {
        Integer max = dao.getMaxSort();
        return max == null ? 0 : max;
    }

    /**
     * 检查用户是否具有某个资源编码的权限
     *
     * @param userId       用户ID
     * @param resourceCode 资源编码
     * @return
     */
    public boolean isPermittedResourceCodeWithPermission(String userId, String resourceCode) {
        Assert.notNull(userId, "参数[userId]为空!");
        Assert.notNull(resourceCode, "参数[resourceCode]为空!");
        //如果是超级管理员 直接允许被授权
        if (SecurityUtils.isUserAdmin(userId)) {
            return true;
        }
        return isUserPermittedResourceCode(userId, resourceCode);
    }

    /**
     * 检查用户是否具有某个资源编码的权限
     *
     * @param userId       用户ID
     * @param resourceCode 资源编码
     * @return
     */
    public boolean isUserPermittedResourceCode(String userId, String resourceCode) {
        Assert.notNull(userId, "参数[userId]为空!");
        Assert.notNull(resourceCode, "参数[resourceCode]为空!");
        List<Resource> list = this.findAuthorityResourcesByUserId(userId);
        boolean flag = false;
        for (Resource resource : list) {
            if (resource != null && StringUtils.isNotBlank(resource.getCode()) && resource.getCode().equalsIgnoreCase(resourceCode)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 根据请求地址判断用户是否有权访问该url
     *
     * @param userId     用户ID
     * @param requestUrl 请求URL地址
     * @return
     */
    public boolean isPermittedResourceMarkUrlWithPermissions(String userId, String requestUrl) {
        //如果是超级管理员 直接允许被授权
        if (SecurityUtils.isUserAdmin(userId)) {
            return true;
        }
        return isPermittedResourceMarkUrlWithPermissions(userId, requestUrl);
    }

    /**
     * 根据请求地址判断用户是否有权访问该url
     *
     * @param userId     用户ID
     * @param requestUrl 请求URL地址
     * @return
     */
    public boolean isUserPermittedResourceMarkUrl(String userId, String requestUrl) {
        //如果是超级管理员 直接允许被授权
        if (SecurityUtils.isUserAdmin(userId)) {
            return true;
        }
        //检查该URL是否需要拦截
        boolean isInterceptorUrl = this.isInterceptorUrl(requestUrl);
        if (isInterceptorUrl) {
            //用户权限Lo
            List<String> userAuthoritys = this.getUserAuthoritysByUserId(userId);
            for (String markUrl : userAuthoritys) {
                String[] markUrls = markUrl.split(";");
                for (int i = 0; i < markUrls.length; i++) {
                    if (StringUtils.isNotBlank(markUrls[i]) && StringUtils.simpleWildcardMatch(markUrls[i], requestUrl)) {
                        return true;
                    }
                }
            }
            return false;
        }
        logger.debug("缓存:{}", CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE + "参数：requestUrl=" + requestUrl + ",userId=" + userId);
        return true;
    }


    /**
     * 查找需要拦截的所有url规则
     *
     * @return
     */
    public List<String> getAllInterceptorUrls() {
        List<String> markUrls = Lists.newArrayList();
        //查找所有资源
        List<Resource> resources = this.findResources();
        for (Resource resource : resources) {
            if (StringUtils.isNotBlank(resource.getMarkUrl())) {
                markUrls.add(resource.getMarkUrl());
            }
        }
        return markUrls;
    }

    /**
     * 检查某个URL是都需要拦截
     *
     * @param requestUrl 检查的URL地址
     * @return
     */
    public boolean isInterceptorUrl(String requestUrl) {
        List<String> markUrlList = this.getAllInterceptorUrls();
        for (String markUrl : markUrlList) {
            String[] markUrls = markUrl.split(";");
            for (int i = 0; i < markUrls.length; i++) {
                if (StringUtils.isNotBlank(markUrls[i]) && StringUtils.simpleWildcardMatch(markUrls[i], requestUrl)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 根据用户ID查找用户拥有的URL权限
     *
     * @param userId 用户ID
     * @return List<String> 用户拥有的markUrl地址
     */
    public List<String> getUserAuthoritysByUserId(String userId) {
        List<String> userAuthoritys = Lists.newArrayList();
        List<TreeNode> treeNodes = this.findTreeNodeResourcesWithPermissions(userId);
        for (TreeNode node : treeNodes) {
            Object obj = node.getAttributes().get("markUrl");
            if (obj != null) {
                String markUrl = (String) obj;
                if (StringUtils.isNotBlank(markUrl)) {
                    userAuthoritys.add(markUrl);
                }
            }
        }
        return userAuthoritys;
    }


    /**
     * 自定义SQL查询
     * @param whereSQL
     * @return
     */
    public List<Resource> findByWhereSQL(String whereSQL){
        Parameter parameter = Parameter.newParameter();
        parameter.put("whereSQL",whereSQL);
        return dao.findByWhereSQL(parameter);
    }

    /**
     * 自定义SQL查询
     * @param sql
     * @return
     */
    public List<Resource> findBySql(String sql){
        Parameter parameter = Parameter.newParameter();
        parameter.put("sql",sql);
        return dao.findBySql(parameter);
    }


    /**    外部接口同步到资源  **/

    /**
     * 同步
     *
     * @param code       编码
     * @param name       资源名称
     * @param parentCode 上级编码
     * @param status     是否启用 默认值：启用 {@link StatusState}
     * @return
     */
    public void iSynchronous(String resourceType, String code, String name, String parentCode, String status) {
        Validate.notNull(code, "参数[code]不能为null");
        if (status == null) {
            status = StatusState.NORMAL.getValue();
        }
        Resource parentResource = null;
        if (StringUtils.isNotBlank(parentCode)) {
            parentResource = this.iGetReource(resourceType, parentCode, null);
            if (parentResource == null) {
                throw new SystemException("上级[" + parentCode + "]不存在.");
            }
        }

        Resource resource = null;
        if (StringUtils.isNotBlank(code)) {
            resource = this.iGetReource(resourceType, code, null);
        }

        if (resource == null) {
            resource = new Resource();
            resource.setStatus(status);
        }

        resource.setName(name);
        resource.setCode(code);
        resource.setType(resourceType);
        resource.setParent(parentResource);
        this.save(resource);
    }

    /**
     * 删除资源
     *
     * @param resourceType 资源类型
     * @param code         资源编码
     */
    public void iDeleteResource(String resourceType, String code) {
        Resource resource = iGetReource(resourceType, code, null);
        if (resource == null) {
            throw new SystemException("编码[" + code + "]不存在.");
        }
        List<String> ids = Lists.newArrayList();
        ids.add(resource.getId());
        this.deleteByIds(ids);
    }

    /**
     * @param resourceType 资源类型
     * @param code         资源编码
     * @param status
     * @return
     */
    public Resource iGetReource(String resourceType, String code, String status) {
        Validate.notNull(code, "参数[code]不能为null");
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, status);
        parameter.put("code", code);
        parameter.put("type", resourceType);
        List<Resource> list = dao.findCustomQuery(parameter);
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * 获取资源
     *
     * @return
     */
    public List<Resource> iGetResources(String resourceType) {
        Parameter parameter = new Parameter();
        parameter.put("type", resourceType);
        return dao.findCustomQuery(parameter);
    }

    /**    外部接口同步到资源  **/

}


