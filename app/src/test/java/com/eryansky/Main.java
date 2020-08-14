/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.encode.Encryption;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-06-17 
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(Encrypt.md5("1"));//79b2cf0337180351d2dcc5ee9d625481
        System.out.println(Encrypt.md5("jxyc@123"));//1e46df17f20fe9c1da92e62e81b76281
        System.out.println(Encrypt.e("jxjfhp@123_2"));//363f38e310c2dbc1d7cb3df2b2eb4d52
        System.out.println(Encrypt.e("15879034026wcp"));//363f38e310c2dbc1d7cb3df2b2eb4d52

    }
}
