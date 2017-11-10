package zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;

/**
 * zookeeper 锁功能的演示 基于curator
 * Created by Administrator on 2017/11/10.
 *
 *  共享锁: 全局同步分布式锁, 同一时间两台机器只有一台能获得同一把锁.
 *  共享读写锁: 用于分布式的读写互斥处理, 同时生成两个锁:一个读锁, 一个写锁,
 *        读锁能被多个应用持有, 而写锁只能一个独占, 当写锁未被持有时, 多个读锁持有者可以同时进行读操作
 *  共享信号量: 在分布式系统中的各个JVM使用同一个zk lock path,
 *      该path将跟一个给定数量的租约(lease)相关联, 然后各个应用根据请求顺序获得对应的lease,
 *      相对来说, 这是最公平的锁服务使用方式.
 *  多共享锁:内部构件多个共享锁(会跟一个znode path关联), 在acquire()过程中,
 *     执行所有共享锁的acquire()方法, 如果中间出现一个失败, 则将释放所有已require的共享锁;
 *      执行release()方法时, 则执行内部多个共享锁的release方法(如果出现失败将忽略)
 */
public class ZooKeeperLockDemo {
    private static CuratorFramework client;


}
