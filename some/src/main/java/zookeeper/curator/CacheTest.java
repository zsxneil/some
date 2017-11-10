package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryNTimes;

/**
 * Created by Administrator on 2017/11/9.
 */
public class CacheTest {
    public static void main(String[] args) throws Exception {
        String connectConfig = "172.20.183.137:2181";
        RetryPolicy policy = new RetryNTimes(5,5000);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectConfig)
                .retryPolicy(policy)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(5000)
                .build();

        client.start();

        //节点监听
        final NodeCache nCache = new NodeCache(client,"/curator/test");
        nCache.start();
        nCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                byte[] data = nCache.getCurrentData().getData();
                System.out.println("当前节点的数据：" + new String(data));
            }
        });

        //子节点监听
        final PathChildrenCache pcCache = new PathChildrenCache(client,"/curator",true);//true指当子节点变化时，获取子节点内容
        pcCache.start();

        pcCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {//重写监听方法
                switch (event.getType()) {//子节点的事件类型
                    case CHILD_ADDED:
                        System.out.println("add:" + event.getData());//通过pathChildrenCacheEvent，可以获取到节点相关的数据
                        break;
                    case CHILD_UPDATED:
                        System.out.println("update:" + event.getData());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("remove:" + event.getData().getPath());
                        break;
                    default:
                        System.out.println("other:" + event.getType());
                }
            }
        });
        Thread.sleep(Long.MAX_VALUE);
        //client.close();
    }
}
