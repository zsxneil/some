package com.my.multi.unit;

import java.util.concurrent.locks.LockSupport;

/**
 * 最终线程会打印出thread over.true。
 * 这说明 线程如果因为调用park而阻塞的话，
 * 能够响应中断请求(中断状态被设置成true)，
 * 但是不会抛出InterruptedException
 * Created by Administrator on 2017/12/5.
 */
public class LockSupportDemo4 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(){
            private int count = 0;
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                long end = 0;
                while (end - start < 1000) {
                    count++;
                    end = System.currentTimeMillis();
                }
                //等待获取许可
                LockSupport.park();
                System.out.println("thread over." + Thread.currentThread().isInterrupted());
            }
        };

        thread.start();
        Thread.sleep(2000);
        // 中断线程
        thread.interrupt();
        System.out.println("main over");
    }
}
