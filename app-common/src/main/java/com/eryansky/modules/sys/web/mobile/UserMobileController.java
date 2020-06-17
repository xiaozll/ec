package com.eryansky.modules.sys.web.mobile;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Result;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 用户管理
 */
@Mobile
@Controller
@RequestMapping("${mobilePath}/sys/user")
public class UserMobileController extends SimpleController {

    @Autowired
    private UserService userService;

    @ModelAttribute("model")
    public User get(@RequestParam(required = false) String id,String userId) {
        if (StringUtils.isNotBlank(id)) {
            return userService.get(id);
        } else if (StringUtils.isNotBlank(userId)) {
            return userService.get(userId);
        } else {
            return new User();
        }
    }

    /**
     * 修改密码 页面
     *
     * @param model
     * @param msg
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "password")
    public String password(@ModelAttribute("model")User model, String msg, Model uiModel) {
        if(null == model || StringUtils.isBlank(model.getId())){
            model = SecurityUtils.getCurrentUser();
        }
        uiModel.addAttribute("model",model);
        if(StringUtils.isNotBlank(msg)){
            addMessage(uiModel,msg);
        }
        return "modules/sys/user-password";
    }

    /**
     * 修改密码 保存
     *
     * @param id 用戶ID
     * @param password
     * @param newPassword
     * @return
     */
    @Logging(logType = LogType.access,value = "修改密码")
    @RequestMapping(value = "savePassword")
    @ResponseBody
    public Result savePassword(@RequestParam(name = "id",required = false) String id,
                               @RequestParam(name = "loginName",required = false) String loginName,
                               @RequestParam(name = "password")String password,
                               @RequestParam(name = "newPassword")String newPassword) {
        User model = StringUtils.isNotBlank(id) ? userService.get(id):userService.getUserByLoginName(loginName);
        if (model == null || StringUtils.isBlank(model.getId())) {
            throw new ActionException("用户[" + (null == model ? "":model.getId()) + "]不存在.");
        }
        SessionInfo sessionInfo =  SecurityUtils.getCurrentSessionInfo();
        if (null == sessionInfo || !sessionInfo.getUserId().equals(model.getId())) {
            throw new ActionException("未授权修改账号密码！");
        }
        String originalPassword = model.getPassword(); //数据库存储的原始密码
        String pagePassword = password; //页面输入的原始密码（未加密）
        if (!originalPassword.equals(Encrypt.e(pagePassword))) {
            return Result.warnResult().setMsg("原始密码输入错误！");
        }

        UserUtils.checkSecurity(model.getId(),newPassword);
        //修改本地密码
        List<String> userIds = new ArrayList<String>(1);
        userIds.add(model.getId());
        UserUtils.updateUserPassword(userIds,newPassword);
        return Result.successResult();
    }


    /**
     * 修改个人信息 页面
     *
     * @param model
     * @param msg
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "input")
    public String input(@ModelAttribute("model")User model, String msg, Model uiModel) {
        if(null == model || StringUtils.isBlank(model.getId())){
            model = SecurityUtils.getCurrentUser();
        }
        uiModel.addAttribute("model",model);
        if(StringUtils.isNotBlank(msg)){
            addMessage(uiModel,msg);
        }
        return "modules/sys/user-input";
    }

    /**
     * 修改个人信息 保存
     *
     * @param model
     * @return
     */
    @Logging(logType = LogType.access,value = "修改个人信息")
    @RequestMapping(value = "saveUserInfo")
    @ResponseBody
    public Result saveUserInfo(@ModelAttribute("model")User model) {
        if (model == null || StringUtils.isBlank(model.getId())) {
            throw new ActionException("用户[" + (null == model ? "":model.getId()) + "]不存在.");
        }
        SessionInfo sessionInfo =  SecurityUtils.getCurrentSessionInfo();
        if (null == sessionInfo || !sessionInfo.getUserId().equals(model.getId())) {
            throw new ActionException("未授权修改账号信息！");
        }
        userService.save(model);
        return Result.successResult();
    }


    /**
     * 通讯录 全部
     *
     * @param companyId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "contactData")
    public String contactData(String companyId,HttpServletRequest request, HttpServletResponse response) {
        List<User> personPlatformContacts = StringUtils.isBlank(companyId) ? userService.findAllNormal():userService.findUsersByCompanyId(companyId);
        Map<String, List<User>> listMap = Maps.newConcurrentMap();
        personPlatformContacts.parallelStream().forEach(v->{
            List<User> list = listMap.get(v.getNamePinyinHeadChar());
            if (Collections3.isEmpty(list)) {
                list = Lists.newCopyOnWriteArrayList();
                list.add(v);
            } else {
                list.add(v);
            }
            listMap.put(v.getNamePinyinHeadChar(), list);
        });
        Set<String> keySet = listMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            List<User> userList = listMap.get(key);
            userList.sort(Comparator.comparing(User::getName));
        }
        Result result = Result.successResult().setObj(listMap);
        String json = JsonMapper.getInstance().toJson(result, User.class, new String[]{"id", "name","mobile"});
        return renderString(response,json, WebUtils.JSON_TYPE);
    }

    /**
     * 通讯录 全部
     *
     * @param companyId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "contactTagData")
    public String contactTagData(String companyId,HttpServletRequest request, HttpServletResponse response) {
        List<User> personPlatformContacts = StringUtils.isBlank(companyId) ? userService.findAllNormal():userService.findUsersByCompanyId(companyId);
        List<Map<String,Object>> list = Lists.newArrayList();
        personPlatformContacts.parallelStream().forEach(v->{
            Map<String,Object> map = Maps.newHashMap();
            map.put("id",v.getId());
            map.put("name",v.getName());
            map.put("phone",v.getMobile());
            map.put("tagIndex",v.getNamePinyinHeadChar());
            list.add(map);
        });
        list.sort(Comparator.nullsLast(Comparator.comparing(m -> (String) m.get("name"),
                Comparator.nullsLast(Comparator.naturalOrder()))));
        return renderString(response,Result.successResult().setObj(list));
    }

    /**
     * 详细信息
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"detail"})
    @ResponseBody
    public Result detail(@ModelAttribute("model") User model) {
        return Result.successResult().setObj(model);
    }


    /**
     * 详细信息
     *
     * @param loginName
     * @return
     */
    @RequestMapping(value = {"detailByLoginName"})
    @ResponseBody
    public Result detailByLoginName(String loginName) {
        User model = UserUtils.getUserByLoginName(loginName);
        return Result.successResult().setObj(model);
    }
}