package com.my.multi.unit;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 从结果可以看出，当四个线程都到达barrier状态后，会从四个线程中选择最后一个执行完的线程去执行Runnable。
 *
 * CyclicBarrier实际用途：假设我们要导入一批数据，因为数据量较大有4亿条，所以我们希望启动四个线程，
 * 每个线程负责导入1亿条数据，我们希望所有的数据导入完成之后，
 * 还在数据库中插入一条记录，表示任务执行完成，此时就可以使用CyclicBarrier。
 *
 * Created by Administrator on 2017/12/6.
 */
public class CyclicBarrierDemo1 {
    public static void main(String[] args) {
        int N = 4;
        CyclicBarrier barrier = new CyclicBarrier(4,new Runnable(){
            @Override
            public void run() {
                System.out.println("当前线程为：" + Thread.currentThread().getName());
            }
        });
        for (int i = 0; i < N; i++) {
            new Writer(barrier).start();
        }
    }

    static class Writer extends Thread{
        private CyclicBarrier barrier;

        public Writer(CyclicBarrier barrier){
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " is writing data");
                Thread.currentThread().sleep(new Random().nextInt(5000));
                System.out.println(Thread.currentThread().getName() + " write data finished, waiting for other thread finish");
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("all thread finished ,continue handle other tasks...");
        }
    }
}
