package com.my.multi.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
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
