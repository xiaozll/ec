/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.encode.Encryption;
import com.eryansky.core.security.jwt.JWTUtils;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-06-17 
 */
public class Main {
    public static void main(String[] args) throws Exception {
        String loginName = "WENCHUNPING";
//        String token = JWTUtils.sign(loginName,loginName);
        String token = JWTUtils.sign(loginName,loginName,10* 1000);
        System.out.println(token);//79b2cf0337180351d2dcc5ee9d625481
        try {
            System.out.println(JWTUtils.verify(token,loginName,loginName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadUtils.sleep(6*1000);
        try {
            System.out.println(JWTUtils.verify(token,loginName,loginName));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
