/**
 * Copyright (c) 2014-2018 http://www.jfit.com.cn
 * <p>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.vo;

/**
 * @author 温春平@wencp jfwencp@jx.tobacco.gov.cn
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
