package com.my.multi.unit;

import java.util.concurrent.CountDownLatch;

/**
 * Created by neil on 2017/12/5.
 */
public class CountDownDemo {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(2);
        new Thread("Thread A"){
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + "start to work, current itme is " + System.currentTimeMillis());
                    Thread.currentThread().sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " end work ,current time is " + System.currentTimeMillis());
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread("Thread B"){
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + "start to work, current itme is " + System.currentTimeMillis());
                    Thread.currentThread().sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " end work ,current time is " + System.currentTimeMillis());
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        try {
            System.out.println("wait for 2 thread finish...");
            latch.await();
            System.out.println("the 2 thread finished their work");
            System.out.println("main thread continue...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
