package com.my.multi.unit.mutex;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 实现
 * Created by Administrator on 2017/12/5.
 */
public class MutexImpl implements Mutex {
    // 仅需要将操作代理到Sync上即可
    private Sync sync = new Sync();
    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void release() {
        sync.release(1);
    }

    //独占式同步组件实现
    private static class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean tryAcquire(int arg) {
            return compareAndSetState(0,1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            return compareAndSetState(1,0);
        }
    }
}
