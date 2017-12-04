package com.my.api;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/4.
 */
public class SleepDemo {
    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                //我们希望不断的去检查服务器的状态，所以讲将检查的代码放入一个死循环中
                while (true){
                    //用打印一句话表示检查服务器状态
                    System.out.println("检查服务器状态....当前时间:"+new Date().toLocaleString());
                    try {
                        //休眠3秒，以免检查台频繁
                        this.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
