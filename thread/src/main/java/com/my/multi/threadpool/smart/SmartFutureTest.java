package com.my.multi.threadpool.smart;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/6.
 */
public class SmartFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SmartThreadExecutorPool smartThreadExecutorPool =
                new SmartThreadExecutorPool(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        //提交一个任务
        SmartFuture future = smartThreadExecutorPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "当前时间：" + new Date();
            }
        });
        future.addListener(new SmartFutureListener() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("异步回调成功：" + result);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("异步回调失败：" + throwable);
            }
        });
        String syncResult = (String) future.get();
        System.out.println(syncResult);

    }
}

