package com.my.multi.unit;

import java.util.concurrent.CountDownLatch;

/**
 * 主线程就是调用线程，主线中设置了CountDownLatch的值为2，并启动两个线程，
 * 每个线程执行完成之后将CountDownLatch减1，最后主线程中调用了latch.await()。
 * 此时主线程就会等到CountDownLatch值为0时才能继续往下执行。
 * 也是说，必须等到两个线程执行完成之后，才能执行。需要注意的是，
 * 如果CountDownLatch设置的值大于2的话，那么主线程就会一直等待下去，
 * 因为CountDownLatch的值即使减去2次，还是大于0，主线程只能一直等待。
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
