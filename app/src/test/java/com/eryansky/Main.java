/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.http.HttpCompoents;
import com.eryansky.common.utils.http.HttpPoolCompoents;
import org.apache.http.client.fluent.Response;

import java.security.SecureRandom;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-06-17 
 */
public class Main {
    public static void main(String[] args) throws Exception {
        SecureRandom rnd_seed = new SecureRandom();
        System.out.println(rnd_seed.nextInt());

    }
}
