package com.my.deeper;

/**
 * Created by neil on 2017/12/4.
 */
public class VolatileDemo {
    volatile static boolean flag = true;

    public static void main(String[] args) throws InterruptedException {
        //该线程每隔1毫秒，修改一次flag的值
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                flag = !flag;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //主线程通过死循环不断根据flag进行判断是否要执行某段代码
        while (true) {
            Thread.sleep(300);
            if (flag) {
                System.out.println("do some thing...");
            } else {
                System.out.println("...");
            }
        }
    }
}
