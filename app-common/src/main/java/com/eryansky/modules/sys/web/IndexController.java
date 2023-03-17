/**
 * Copyright (c) 2012-2022 https://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.utils.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 主页管理
 *
 * @author Eryan
 * @date 2014-09-16 10:30
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class IndexController extends SimpleController {

    @GetMapping(value = {""})
    public ModelAndView admin(HttpServletRequest request) {
        ModelAndView modelAnView = new ModelAndView("login");
        if (SecurityUtils.getCurrentSessionInfo() != null) {
            return index(request);
        }
        return modelAnView;
    }

    @GetMapping(value = {"index"})
    public ModelAndView index(HttpServletRequest request) {
        User sessionUser = SecurityUtils.getCurrentUser();
        String userPhoto = null;
        if (sessionUser != null && StringUtils.isNotBlank(sessionUser.getPhoto())) {
            userPhoto = sessionUser.getPhotoUrl();
        }

        if (StringUtils.isBlank(userPhoto)) {
            userPhoto = SpringMVCHolder.getRequest().getContextPath() + "/static/img/icon_boy.png";
        }
        ModelAndView modelAnView = new ModelAndView("layout/index.html");
        modelAnView.addObject("userPhoto", userPhoto);
        modelAnView.addObject("isMobile", UserAgentUtils.isMobile(request));
        return modelAnView;
    }

    @GetMapping(value = "index/west")
    public ModelAndView west() {
        ModelAndView modelAnView = new ModelAndView("layout/west");
        User sessionUser = SecurityUtils.getCurrentUser();
        modelAnView.addObject("user", sessionUser);
        String userPhoto = null;
        if (null != sessionUser && StringUtils.isNotBlank(sessionUser.getPhoto())) {
            userPhoto = sessionUser.getPhotoUrl();
        } else {
            userPhoto = SpringMVCHolder.getRequest().getContextPath() + "/static/img/icon_boy.png";
        }
        modelAnView.addObject("userPhoto", userPhoto);
        return modelAnView;
    }


}
