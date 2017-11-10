package zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Created by Administrator on 2017/11/10.
 */
public class LeaderElection {
    private static final String conConfg = "172.20.183.137:2181";
    private static final String leaderPath = "/curator/leader";

    public static void main(String[] args) {
        LeaderSelectorListener listener = new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println(Thread.currentThread().getName() + " take leadership");
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + " relinquish leadership");
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

            }
        };

        new Thread(()->{
            registerListener(listener);
        },"t1").start();
        new Thread(()->{
            registerListener(listener);
        },"t2").start();
        new Thread(()->{
            registerListener(listener);
        },"t3").start();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void registerListener(LeaderSelectorListener listener) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(conConfg)
                .retryPolicy(new RetryNTimes(5,5000))
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .build();

        client.start();

        try {
            Stat stat = client.checkExists().forPath(leaderPath);
            if (stat == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(leaderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LeaderSelector selector = new LeaderSelector(client, leaderPath,listener);
        selector.autoRequeue();
        selector.start();
    }
}
