/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.vo;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-06-14 
 */
public class PasswordTip {

    public static final int CODE_NO = 0;
    public static final int CODE_YES = 1;

    private int code;
    private String msg;

    public PasswordTip() {
        this.code = CODE_NO;
    }

    public PasswordTip(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isTip() {
        return CODE_YES == code;
    }

}
