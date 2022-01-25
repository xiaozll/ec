/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.orm.mybatis.sensitive.IEncrypt;
import com.eryansky.common.orm.mybatis.sensitive.encrypt.AesSupport;
import com.eryansky.common.utils.encode.Cryptos;
import com.eryansky.common.utils.encode.EncryptionSafe;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-06-17 
 */
public class Main {
    public static void main(String[] args) throws Exception {
        EncryptionSafe as = new EncryptionSafe();
        System.out.println(as.encrypt("1"));//7e0cd7be3e66d4a8
        System.out.println(as.decrypt(as.encrypt("1")));

        IEncrypt iEncrypt = new AesSupport();
        System.out.println(iEncrypt.encrypt("1"));;
        System.out.println(iEncrypt.decrypt(iEncrypt.encrypt("1")));;

    }
}
