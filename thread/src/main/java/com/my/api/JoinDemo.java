package com.my.api;

/**
 * Created by Administrator on 2017/12/4.
 */
public class JoinDemo {
    public static void main(String[] args) throws InterruptedException {
        long start=System.currentTimeMillis();
        System.out.println("程序开始运行...");
        Thread t = new Thread(){
            @Override
            public void run() {
                try {//模拟自定义线程干某个事花了5秒
                    System.out.println("自定义线程执行开始...");
                    Thread.sleep(5000);
                    System.out.println("自定义线程执行完毕...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        //模拟主线干其他事花了2秒
        Thread.sleep(2000);
        System.out.println("主线程执行完毕，等待t线程执行完成...");
        //主线程2秒后就可以继续执行，但是其必须执行的条件是t线程必须执行完成，此时调用t的join方法
        long joinstart=System.currentTimeMillis();
        t.join();
        System.out.println("主线程：t执行已经执行完毕...，等待了"+(System.currentTimeMillis()-joinstart)/1000+"秒");
        System.out.println("程序运行总时间..."+(System.currentTimeMillis()-start)/1000+"秒");
    }
}
