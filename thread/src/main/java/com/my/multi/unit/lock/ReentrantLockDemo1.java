package com.my.multi.unit.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 从输出结果中可以看到，A获取到锁，先执行，而必须等到A执行完成之后，B才能执行。
 * 因此Lock对象的实际上是一个独占锁。
 * Created by neil on 2017/12/5.
 */
public class ReentrantLockDemo1 {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        new Thread("thread A"){
            @Override
            public void run() {
                lock.lock();
                try {
                    work();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }

            }
        }.start();
        new Thread("thread B"){
            @Override
            public void run() {
                lock.lock();
                try {
                    work();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }

            }
        }.start();
    }

    public static void work() {
        try {
            System.out.println(Thread.currentThread().getName() + " start to work ,current time is " + System.currentTimeMillis());
            Thread.currentThread().sleep(1000);
            System.out.println(Thread.currentThread().getName() + " end work ,current time is " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
