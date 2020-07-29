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
        System.out.println(Encryption.decrypt("7e0cd7be3e66d4a8"));
        System.out.println(Encrypt.e(Encryption.decrypt("7e0cd7be3e66d4a8")));

    }
}
