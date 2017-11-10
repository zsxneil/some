package zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 共享信号量: 在分布式系统中的各个JVM使用同一个zk lock path,
 *      该path将跟一个给定数量的租约(lease)相关联, 然后各个应用根据请求顺序获得对应的lease,
 *      相对来说, 这是最公平的锁服务使用方式.
 * Created by Administrator on 2017/11/10.
 */
public class SharedSemaphoreTest {
    private static final String conConfg = "172.20.183.137:2181";
    private static final String lockPath = "/curator/semaphore";
    private static final String countPath = "/curator/count";

    private static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(conConfg)
            .retryPolicy(new RetryNTimes(5,5000))
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(5000)
            .build();
    public static void main(String[] args) {

        client.start();

        for (int i=0;i<4;i++) {
            testSharedSemaphore(i);
        }

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.close();

    }

    private static void testSharedSemaphore(int n) {
        final InterProcessSemaphoreV2 semaphoreV2 = new InterProcessSemaphoreV2(client,lockPath,
                new SharedCount(client,countPath,2));
        List<Thread> jobs = new ArrayList<>();
        for(int j=0;j<2;j++){
            jobs.add(new Thread(()->{
                sharedSemaphore(semaphoreV2);
            },n + "共享信息锁" + j));
        }

        for (Thread t : jobs) {
            t.start();
        }
    }

    /**
     * 共享信号量
     * 设置总的数量 -->分布式情况下的最大并行数量
     * 按照请求顺序进行 执行权的分配
     * 可以设置超时 不执行 也可以设置 直到获取执行权 执行
     */
    private static void sharedSemaphore(InterProcessSemaphoreV2 semaphoreV2) {
        Lease lease = null;

        try {
            lease = semaphoreV2.acquire();
            if (lease != null) {
                System.out.println(Thread.currentThread().getName()+"   执行任务开始" + System.currentTimeMillis());
                //Thread.sleep(RandomUtils.nextInt(4000));
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+"   执行任务完毕" + System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (lease != null) {
                    semaphoreV2.returnLease(lease);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
