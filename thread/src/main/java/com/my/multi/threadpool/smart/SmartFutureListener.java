package com.my.multi.threadpool.smart;

/**
 * Created by Administrator on 2017/12/6.
 */
public interface SmartFutureListener<V> {
    public void onSuccess(V result);
    public void onError(Throwable throwable);
}
