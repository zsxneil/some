package com.my.multi.unit.mutex;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 可以看到，我们的独占锁的确是起作用了，任意一时刻只有一个线程在运行。
 请读者注意输出结果线程启动的顺序：1，0，2，3,4。线程1先获取到了锁并执行，
 而0、2、3、4被加入到了等待队列。
 而后面获取到锁的顺序也是0,2,3,4。
 这是因为AQS内部是使用一个FIFO队列，所以先进入等待队列的先获取到锁。
 * Created by Administrator on 2017/12/5.
 */
public class MutexMain {
    public static void main(String[] args) {
        Mutex mutex = new MutexImpl();
        for (int i = 0; i < 5; i++) {
            new MutexThread("线程" + i,mutex).start();
        }
    }

    static class MutexThread extends Thread{

        private Mutex mutex;

        public MutexThread(String name,Mutex mutex){
            this.mutex = mutex;
            this.setName(name);
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "启动...");
            mutex.lock();
            System.out.println(Thread.currentThread().getName() + "获取锁成功");
            try {
                System.out.println(Thread.currentThread().getName() + "开始执行，当前时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                Thread.currentThread().sleep(1000);//假设线程执行需要1秒钟
                System.out.println(Thread.currentThread().getName() + "结束执行，当前时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + "释放锁");
                mutex.release();
            }
        }
    }
}
