package com.my.multi.threadpool.smart;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.FutureTask;

/**
 * 实现一个类似功能的Future实现，在这里称之为SmartFuture。
 * 其支持同步等待返回结果，也支持异步通知结果。由于FutureTask已经提供了同步等待的功能，
 * 所以我们只需要让我们的SmartFuture继承FutureTask，再添加相关异步功能的方法即可
 * Created by Administrator on 2017/12/6.
 */
public class SmartFuture<V> extends FutureTask<V> {
    //异步通知的listener
    private Set<SmartFutureListener> listeners = null;

    //任务运行结果
    Object result = null;
    private boolean hasResult;
    public SmartFuture(Callable<V> callable) {
        super(callable);
        listeners = new CopyOnWriteArraySet<>();
    }

    public SmartFuture(Runnable runnable, V result) {
        super(runnable, result);
        listeners = new CopyOnWriteArraySet<>();
    }

    public void addListener(SmartFutureListener listener) {
        if (listener == null)
            throw new NullPointerException();
        if (hasResult) {//如果添加listener的时候，任务已经执行完成，直接回调listener
            nofityListener(listener);
        } else {//如果任务没有执行完成，添加到监听队列
            listeners.add(listener);
        }
    }

    //覆写set方法，结果运行成功
    @Override
    protected void set(V v) {
        super.set(v);
        result = v;
        hasResult = true;
        nofityListeners();
    }

    //覆写 setException方法
    @Override
    protected void setException(Throwable t) {
        super.setException(t);
        result = t;
        hasResult = true;
        nofityListeners();
    }

    //回调
    private void nofityListeners(){
        for (SmartFutureListener listener : listeners) {
            nofityListener(listener);
        }
    }

    private void nofityListener(SmartFutureListener listener) {
        if (result instanceof Throwable) {
            listener.onError((Throwable) result);
        } else {
            listener.onSuccess(result);
        }
        listeners = null;//不明觉厉
    }

}
