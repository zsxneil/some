package com.my.multi.unit;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport是不可重入 的，如果一个线程连续2次调用 LockSupport .park()，那么该线程一定会一直阻塞下去。
 * Created by Administrator on 2017/12/5.
 */
public class LockSupportDemo3 {
    public static void main(String[] args) {
        Thread thread = Thread.currentThread();
        LockSupport.unpark(thread);
        System.out.println("a");
        LockSupport.park();
        System.out.println("b");
        LockSupport.park();
        System.out.println("c");
    }
}
