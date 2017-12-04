package com.my.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2017/12/4.
 */
public class InterruptDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(){
            @Override
            public void run() {
                int i=0;
                while (true) {
                    //每次打印前都判断是否被中断
                    if (!Thread.interrupted()) {
                        i++;
                        System.out.println("自定义线程，打印...." + i + "次");
                    } else {//如果被中断，停止运行
                        System.out.println("自定义线程：被中断...");
                        return;
                    }
                }
            }
        };
        t.start();

        //主线程休眠1毫秒，以便自定义线程执行
        Thread.sleep(1);
        System.out.println("主线程:休眠1毫秒后发送中断信号...");
        t.interrupt();
    }
}
