package com.eryansky.utils;

import com.eryansky.common.utils.encode.EncryptionSafe;

/**
 * @author
 * @date 2019-08-07 
 */
public class DruidPassword {
    public static void main(String[] args) throws Exception {

        System.out.println(EncryptionSafe.encrypt("password"));
    }
}
