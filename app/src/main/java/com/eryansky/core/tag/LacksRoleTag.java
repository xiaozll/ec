/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.tag;


import com.eryansky.core.security.SecurityUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * 判断是否不具备某个角色编码权限
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-18
 */
@SuppressWarnings("serial")
public class LacksRoleTag extends TagSupport {

    /**
     * 角色编码访问权限字符串 多个以";"分割
     */
    private String name = "";

    @Override
    public int doStartTag() throws JspException {
        if (!"".equals(this.name)) {
            String[] permissonNames = name.split(";");
            for (String permissonName : permissonNames) {
                if (!SecurityUtils.isPermittedRole(permissonName)) {
                    return TagSupport.EVAL_BODY_INCLUDE;
                }
            }

        }
        return SKIP_BODY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}