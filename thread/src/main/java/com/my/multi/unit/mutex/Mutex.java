package com.my.multi.unit.mutex;

/**
 * Created by Administrator on 2017/12/5.
 */
//不可重入的独占锁接口
public interface Mutex {
    //获取锁
    public void lock();
    //释放锁
    public void release();
}
