package com.my.multi.threadpool;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/12/6.
 */
public class ThreadPoolExecutorTest {

    //测试corePoolSize和MaximumPoolSize随着任务提交量的变化,以及keepAliveTime与TimeUnit
    @Test
    public void threadPoolExecutorTest() throws InterruptedException {
        int corePoolSize = 2;
        int maxPoolSize = 5;
        int keepAliveTime = 5;
        int taskCount = 5;

        //**********立即提交的情况下，taskCount>maxPoolSize的情况******************
        //int taskCount = 6;
        //BlockingQueue workQueue = new SynchronousQueue();
        //********************************************************************

        //**********有界队列的情况下，taskCount>maxPoolSize+taskQueueSize的情况*************
        //int taskCount = 9;//将taskCount改为10，超出maxPoolSize设置的5
        //BlockingQueue workQueue = new LinkedBlockingDeque<>(4);//队列的大小设置为4
        //*****************************************************************************


        //**********无界队列的情况下,如果构建 LinkedBlockingDeque的时候，没有指定大小，即使用了无界的任务队列***********
        BlockingQueue workQueue = new LinkedBlockingDeque<>();
        //************************************************************************************************

        TimeUnit seconds = TimeUnit.SECONDS;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, seconds, workQueue);

        //************使用自定义的线程工程类******************
        threadPoolExecutor.setThreadFactory(new NamedThreadFactory());
        //***********************************************

        doTest(keepAliveTime,taskCount,threadPoolExecutor);
                
    }

    private void doTest(int keepAliveTime, int taskCount, ThreadPoolExecutor threadPoolExecutor) throws InterruptedException {
        //threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        System.out.println("-------threadPoolExecutor刚刚创建----------");
        printPoolSize(threadPoolExecutor);

        for (int i=0;i<taskCount;i++){
            threadPoolExecutor.execute(new Task(threadPoolExecutor,i));
            System.out.print("--------已提交任务"+ i +"个任务--------");
            printPoolSize(threadPoolExecutor);
        }

        //等到所有的任务都执行完
        TimeUnit.SECONDS.sleep(30);//休眠11秒
        System.out.println("---------所有的任务都执行完--------");
        printPoolSize(threadPoolExecutor);

        //此时maximumPoolSize>corePoolSize，当前时间再休眠keepAliveTime时间，测试多出corePoolSize的线程是否能自动销毁
        System.out.println("---------休眠keepAliveTime，测试maximumPoolSize>corePoolSize的部分能否自动回收--------");
        TimeUnit.SECONDS.sleep(keepAliveTime);
        printPoolSize(threadPoolExecutor);
    }


    private void printPoolSize(ThreadPoolExecutor executor) {
        int corePoolSize = executor.getCorePoolSize();
        int maxPoolSize = executor.getMaximumPoolSize();
        int poolSize = executor.getPoolSize();
        System.out.println("核心线程池大小：" + corePoolSize + ";最大线程池大小：" + maxPoolSize + ";当前线程池大小：" + poolSize);
    }

    class Task implements Runnable{

        private ThreadPoolExecutor threadPoolExecutor;
        private int taskId;

        public Task(ThreadPoolExecutor threadPoolExecutor,final int taskId) {
            this.threadPoolExecutor = threadPoolExecutor;
            this.taskId = taskId;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(2);//休眠10秒
                System.out.println("第" + taskId + "个任务执行完成,ThreadName:" + Thread.currentThread().getName());
                printPoolSize(threadPoolExecutor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
