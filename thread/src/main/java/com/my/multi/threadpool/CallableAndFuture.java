package com.my.multi.threadpool;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/12/6.
 */
public class CallableAndFuture {
    public static void main(String[] args) {
        System.out.println("开始时间：" + new Date());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Future与Callable中的泛型，就是返回值的类型
        Future future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(2);
                return "Hello";
            }
        });
        try {
            String result = (String) future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("结束时间：" + new Date());
        executorService.shutdown();

    }
}
