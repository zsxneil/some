package com.my.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2017/12/4.
 */
public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(){
            @Override
            public void run() {
                //预期循环10次
                for (int i = 0; i <10 ; i++) {
                    try {
                        Thread.sleep(4000);
                        System.out.println("自定义线程:当前时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("自定义线程:收到中断信号，总共循环了"+i+"次...");
                        return;
                    }
                }
            }
        };
        t.start();

        Thread.sleep(9000);
        System.out.println("主线程：等待9秒后发送中断信号...");
        t.interrupt();
    }
}
