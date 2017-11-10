package zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/11/10.
 */
public class SharedReentrantLockTest {
    private static final String conConfg = "172.20.183.137:2181";
    private static final String lockPath = "/curator/lock";

    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(conConfg)
                .retryPolicy(new RetryNTimes(5,5000))
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .build();
        CuratorFramework client1 = CuratorFrameworkFactory.builder()
                .connectString(conConfg)
                .retryPolicy(new RetryNTimes(5,5000))
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .build();



        client.start();
        client1.start();
        System.out.println("zk client start successfully");

        Thread thread1 = new Thread(()->{

           doWithLock(client1);
        },"t1");

        Thread thread2 = new Thread(()->{

            doWithLock(client);
        },"t2");

        thread1.start();
        thread2.start();
    }

    private static void doWithLock(CuratorFramework client) {
        InterProcessMutex lock = new InterProcessMutex(client,lockPath);
        try {
            lock.acquire();
            if (lock.acquire(10 * 1000, TimeUnit.SECONDS)) {
                System.out.println("Thread " + Thread.currentThread().getName() + " get the lock");
                Thread.sleep(5000);
                System.out.println("Thread " + Thread.currentThread().getName() + " release the lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
