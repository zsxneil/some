package zookeeper.curator;

import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.RetryNTimes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 共享读写锁: 用于分布式的读写互斥处理, 同时生成两个锁:一个读锁, 一个写锁,
 *        读锁能被多个应用持有, 而写锁只能一个独占, 当写锁未被持有时, 多个读锁持有者可以同时进行读操作
 * Created by Administrator on 2017/11/10.
 */
public class ReadWriteLockTest {

    //创建多线程 循环进行锁的操作
    private static final String conConfg = "172.20.183.137:2181";
    private static final String lockPath = "/curator/readwritelock";
    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(conConfg)
                .retryPolicy(new RetryNTimes(5,5000))
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .build();
        client.start();

        InterProcessReadWriteLock readWriteLock = new InterProcessReadWriteLock(client,lockPath);
        final InterProcessMutex readLock = readWriteLock.readLock();
        final InterProcessMutex writeLock = readWriteLock.writeLock();
        List<Thread> jobs = new ArrayList<>();

        for (int i=0;i<20;i++){
            jobs.add(new Thread(()->{
                readWriteLock(writeLock);
            },"写锁" + i));
        }

        for (int i=0;i<10;i++){
            jobs.add(new Thread(()->{
                readWriteLock(readLock);
            },"读锁" + i));
        }

        for(Thread t : jobs) {
            t.start();
        }

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.close();
    }

    private static void readWriteLock(InterProcessLock lock) {
        System.out.println(Thread.currentThread().getName() + " enter the task -" + System.currentTimeMillis());

        try {
            if (lock.acquire(20, TimeUnit.MICROSECONDS)) {
                //System.err.println(Thread.currentThread().getName() + " 等待超时  无法获取到锁");
                //执行任务 --读取 或者写入
                int time = RandomUtils.nextInt(10,150);
                System.out.println(Thread.currentThread().getName()+"   执行任务开始");
                Thread.sleep(time);
                System.out.println(Thread.currentThread().getName()+"   执行任务完毕");
            } else {
                System.err.println(Thread.currentThread().getName() + " 等待超时  无法获取到锁");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock.isAcquiredInThisProcess())
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
