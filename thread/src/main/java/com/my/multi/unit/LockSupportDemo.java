package com.my.multi.unit;

import java.util.concurrent.locks.LockSupport;

/**
 * 线程一直阻塞，不会结束
 * Created by Administrator on 2017/12/5.
 */
public class LockSupportDemo {
    public static void main(String[] args) {
        Thread thread = Thread.currentThread();
        LockSupport.park();
        System.out.println(".block");
    }
}
