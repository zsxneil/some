package zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/11/10.
 *
 * 共享锁: 全局同步分布式锁, 同一时间两台机器只有一台能获得同一把锁.
 */
public class SharedLockTest {

    private static final String conConfg = "172.20.183.137:2181";
    private static final String lockPath = "/curator/lock";

    public static void main(String[] args) {
        new Thread(()->{
            shareLock();
        },"t1").start();
        new Thread(()->{
            shareLock();
        },"t2").start();
    }

    private static void shareLock() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(conConfg)
                .retryPolicy(new RetryNTimes(5,5000))
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .build();
        client.start();
        //这两个都是共享锁
        // new InterProcessMutex(client, path)
        // new InterProcessSemaphoreMutex(client, path

        InterProcessMutex lock = new InterProcessMutex(client,lockPath);

        //锁是否被获取到
        //超时 不在进行操作
        try {
            if (lock.acquire(3000 * 10, TimeUnit.SECONDS)) {
                System.out.println(Thread.currentThread().getName() + "  is  get the shared lock");
                Thread.sleep(5000L);
                System.out.println(Thread.currentThread().getName() + " is release the shared lock");
            }
        } catch (Exception e) {
            //日志记录一下 超时说明 有锁 可以不在操作
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + " the flag is " + lock.isAcquiredInThisProcess());
            if (lock.isAcquiredInThisProcess()) //判断是否持有锁 进而进行锁是否释放的操作
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            client.close();
        }
    }
}
