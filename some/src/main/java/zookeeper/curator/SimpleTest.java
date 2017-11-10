package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
public class SimpleTest {
    public static void main(String[] args) throws Exception{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);//重试5次，每次间隔时间指数增长(有具体增长公式)
        RetryPolicy retryPolicy1 = new RetryNTimes(5,5000);////重试5次，每次间隔5秒
        RetryPolicy retryPolicy2 = new RetryUntilElapsed(60000 * 2,5000);//重试2分钟，每次间隔5秒

        String connectConfig = "172.20.183.137:2181";
        //普通创建
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectConfig,5000,5000,retryPolicy);
        //fluent风格创建
        CuratorFramework client1 = CuratorFrameworkFactory.builder()
                                    .connectString(connectConfig)
                                    .retryPolicy(retryPolicy)
                                    .sessionTimeoutMs(5000)
                                    .connectionTimeoutMs(5000)
                                    .build();

        client.start();

        String path = client.create()
                .creatingParentsIfNeeded() //节点路径上没有的自动创建
                .withMode(CreateMode.EPHEMERAL)//临时节点
                .forPath("/curator/test","123".getBytes());

        //获取子节点列表
        List<String> paths = client.getChildren().forPath("/curator");
        System.out.println(paths);


        //更新节点信息
        Stat stat = new Stat();
        byte[] theValue = client
                .getData()
                .storingStatIn(stat)
                .forPath("/curator/test");
        System.out.println(new String(theValue));
        client.setData()
                .withVersion(stat.getVersion()) //版本校验，与当前版本不一致则更新失败,-1则无视版本信息进行更新
                .forPath("/curator/test","456".getBytes());
        theValue = client
                .getData()
                .storingStatIn(stat)
                .forPath("/curator/test");
        System.out.println(new String(theValue));

        paths = client.getChildren().forPath("/curator");
        System.out.println(paths);

        //删除节点
        client.delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .forPath("/curator/test");

        Stat stat1 = client.checkExists().forPath("/curator/test");
        if (stat1 == null) {
            System.out.println("delete sucessfully");
        }
        paths = client.getChildren().forPath("/curator");
        System.out.println(paths);

        client.close();

    }
}
