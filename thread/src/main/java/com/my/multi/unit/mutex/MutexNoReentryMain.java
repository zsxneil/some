package com.my.multi.unit.mutex;

/**
 * 上述代码中，永远不会打印出"运行结束"这句话，程序会一直阻塞，因为我们的锁是不可重入的。
 * 说明：所谓不可重入，指的是，一个线程在释放锁之前，不能再次获取这个锁。
 * 上述代码中，第一次调用lock时，主线程获取到锁，可以运行，可以在主线程第二次获取锁的时候，
 * 因为锁已经被占用了，所以第二次无法获取。由于我们对于一个线程无法获取锁时，就
 * 会对其进行阻塞，并加入等待队列。因此第二次获取不到锁，其结果是导致主线程被阻塞了，最终程序就会一直被阻塞。
 * Created by Administrator on 2017/12/5.
 */
public class MutexNoReentryMain {
    public static void main(String[] args) {
        Mutex mutex = new MutexImpl();
        mutex.lock();
        mutex.lock();//重复lock
        System.out.println("main over");
    }
}
