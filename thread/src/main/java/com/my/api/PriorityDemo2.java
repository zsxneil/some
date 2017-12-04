package com.my.api;

import javafx.scene.layout.Priority;

/**
 * Created by Administrator on 2017/12/4.
 */
public class PriorityDemo2 {
    public static void main(String[] args) {
        //创建线程t1并将优先级设为10
        Thread t1 = new Thread("P10"){
            @Override
            public void run() {
                for (int i = 0; i <10 ; i++) {
                    System.out.println(Thread.currentThread().getName()+":执行"+i+"次");
                }
            }
        };
        t1.setPriority(Thread.MAX_PRIORITY);

        //创建线程t2并将优先级设为1
        Thread t2=new Thread("P1"){
            @Override
            public void run() {
                for (int i = 0; i <10 ; i++) {
                    System.out.println(Thread.currentThread().getName()+":执行"+i+"次");
                }
            }
        };
        t2.setPriority(Thread.MIN_PRIORITY);
        t1.start();
        t2.start();
    }
}
