package com.my.compress;

import encrypt.HMAC;

/**
 * Created by Administrator on 2017/11/23.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(HMAC.encryptBase64("".getBytes("utf-8")));
    }
}

