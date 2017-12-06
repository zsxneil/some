package com.my.multi.threadpool.smart;

import java.util.concurrent.*;

/**
 * 要实现异步监听的Future，我们除了要实现Future对象，还要自己实现一个线程池，
 * 因为如果我们直接使用ThreadExecutorPool提交任务(Callable，Runnable)，
 * 其还是会将其包装成一个FutureTask对象，这是通过newFutureTask方法创建的，
 * 我们可以对相关方法进行覆盖，使得返回的对象是SmartFuture
 * Created by Administrator on 2017/12/6.
 */
public class SmartThreadExecutorPool extends ThreadPoolExecutor{
    public SmartThreadExecutorPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new SmartFuture<T>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new SmartFuture<T>(callable);
    }

    @Override
    public SmartFuture<?> submit(Runnable task) {
        return (SmartFuture<?>) super.submit(task);
    }

    @Override
    public <T> SmartFuture<T> submit(Runnable task, T result) {
        return (SmartFuture<T>) super.submit(task, result);
    }

    @Override
    public <T> SmartFuture<T> submit(Callable<T> task) {
        return (SmartFuture<T>) super.submit(task);
    }
}
