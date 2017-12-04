package com.my.lock;

/**
 * Created by neil on 2017/12/4.
 */
public class ThreadCompetitionDemo2 {
    static int count = 0;
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 500000; i++) {
                    synchronized (ThreadCompetitionDemo2.class) {
                        count++;
                    }
                }
                System.out.println("自定义线程:计算完成...，耗时"+(System.currentTimeMillis()-start));
            }
        }.start();
        for (int i = 0; i < 500000; i++) {
            synchronized (ThreadCompetitionDemo2.class) {
                count++;
            }
        }

        System.out.println("主线程:计算完成....，耗时"+(System.currentTimeMillis()-start));
        Thread.sleep(10000);
        System.out.println("count:"+count);

    }
}
