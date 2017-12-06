package com.my.multi.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/12/6.
 */
public class NamedThreadFactory implements ThreadFactory{

    private static AtomicInteger poolId;
    private static ThreadGroup threadGroup;
    private AtomicInteger threadId;
    private static String threadNamePrefix="NamedThreadPool";

    public NamedThreadFactory() {
        poolId = new AtomicInteger();
        threadGroup = new ThreadGroup("NamedThreadFactory");
        threadId = new AtomicInteger();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = threadNamePrefix + "-pool-" + poolId.getAndIncrement() + "-thread-" + threadId;
        Thread t = new Thread(threadGroup,r, name);
        return t;
    }
}
