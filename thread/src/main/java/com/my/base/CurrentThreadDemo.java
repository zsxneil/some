package com.my.base;

/**
 * Created by Administrator on 2017/12/4.
 */
public class CurrentThreadDemo {
    public static void main(String[] args) {
        new Thread("custom thread"){
            @Override
            public void run() {
                System.out.println("当前线程:" + Thread.currentThread().getName());
            }
        }.start();
        System.out.println("当前线程:" + Thread.currentThread().getName());
    }
}
