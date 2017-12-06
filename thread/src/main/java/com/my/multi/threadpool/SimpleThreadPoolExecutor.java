package com.my.multi.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 线程池的实现由两部分组成。类  SimpleThreadPoolExecutor是线程池的公开接口，
 * 而类 WorkerThread 用来实现执行任务的子线程。
 * 为了执行一个任务，方法 SimpleThreadPoolExecutor .execute(Runnable r) 用 Runnable 的实现作为调用参数。
 * 在内部，Runnable 对象被放入 阻塞队列 (Blocking Queue)，等待着被子线程取出队列。
 * 一个空闲的 WorkerThread 线程会把 Runnable 对象从队列中取出并执行。你可以在 WorkerThread .run() 方法里看到这些代码。
 * 执行完毕后，WorkerThread 进入循环并且尝试从队列中再取出一个任务，直到线程终止。
 * 调用 SimpleThreadPoolExecutor .stop() 方法可以停止 SimpleThreadPoolExecutor 。
 * 在内部，调用 stop 先会标记 isStopped 成员变量（为 true）。然后，线程池的每一个子线程都调用 WorkerThread .stop() 方法停止运行。
 * 注意，如果线程池的 execute() 在 stop() 之后调用，execute() 方法会抛出 IllegalStateException 异常。
 * 子线程会在完成当前执行的任务后停止。注意 PoolThread.stop() 方法中调用了 this.interrupt()。
 * 它确保阻塞在 taskQueue.take() 里的处理等待状态的调用的线程能够跳出 等待状态
 * Created by Administrator on 2017/12/6.
 */
public class SimpleThreadPoolExecutor implements Executor{

    private BlockingQueue<Runnable> taskQueue = null;//任务队列
    private List<WorkerThread> threads = new ArrayList<>();//线程池
    private boolean isStoped = false;

    public SimpleThreadPoolExecutor(int noOfThread,int maxNoOfTasks) {
        taskQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < noOfThread; i++) {
            threads.add(new WorkerThread(taskQueue));
        }
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (this.isStoped)
            throw new IllegalStateException("SimpleThreadPoolExecutor is stopped" );
        this.taskQueue.add(command);
    }

    public synchronized void stop() {
        this.isStoped = true;
        for (WorkerThread thread : threads) {
            thread.toStop();//循环中断每一个线程
        }
    }

    class WorkerThread extends Thread{
        private BlockingQueue<Runnable> taskQueue = null;
        private boolean isStopped = false;

        public WorkerThread(BlockingQueue<Runnable> taskQueue) {
            this.taskQueue = taskQueue;
        }

        @Override
        public void run() {
            //因为需要不断从的任务列出中取出task执行，因此需要放在一个循环中，否则线程对象执行完一个任务就会立刻结束
            while (!isStopped) {
                try {
                    Runnable runnable = taskQueue.take();
                    runnable.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        public synchronized void toStop(){
            isStopped = true;
            this.interrupt();//如果线程正在任务队列中获取任务，或者没有任务被阻塞，需要响应这个中断
        }
        
        public synchronized boolean isStopped(){
            return isStopped;
        }
    }
}
