package com.my.multi.unit.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neil on 2017/12/5.
 */
public class ReentrantLockDemo2 {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        System.out.println(lock.getHoldCount());
        lock.lock();
        System.out.println(lock.getHoldCount());
        lock.lock();
        System.out.println(lock.getHoldCount());
        lock.unlock();
        System.out.println(lock.getHoldCount());
        lock.unlock();
        System.out.println(lock.getHoldCount());
    }
}
