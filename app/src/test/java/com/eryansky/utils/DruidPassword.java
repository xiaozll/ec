/**
 * Copyright (c) 2014-2019 http://www.jfit.com.cn
 * <p>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.utils;

import com.eryansky.common.utils.encode.Encryption;

/**
 * @author 温春平 jfwencp@jx.tobacco.gov.cn
 * @date 2019-08-07 
 */
public class DruidPassword {
    public static void main(String[] args) throws Exception {

        System.out.println(Encryption.encrypt("password"));
    }
}
