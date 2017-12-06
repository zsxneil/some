package com.my.multi.unit;

import java.util.concurrent.Semaphore;

/**
 * 假若一个工厂有5台机器，但是有8个工人，
 * 一台机器同时只能被一个工人使用，只有使用完了，
 * 其他工人才能继续使用。那么我们就可以通过Semaphore来实现：
 * Created by neil on 2017/12/5.
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(5);//机器数量
        int num = 8;//工人数量
        for (int i = 0; i < num; i++) {
            new Worker(i,semaphore).start();
        }
    }

    static class Worker extends Thread{
        private int num;
        private Semaphore semaphore;

        public Worker(int num, Semaphore semaphore) {
            this.num = num;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println("worker " + this.num + " is using a machine");
                Thread.currentThread().sleep(2000);
                System.out.println("worker " + this.num + " releas a machine");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }
}
