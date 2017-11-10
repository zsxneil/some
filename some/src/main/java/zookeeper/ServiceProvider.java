package zookeeper;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ServiceProvider {
	private static final Logger log = Logger.getLogger(ServiceProvider.class);
	
	// 用于等待 SyncConnected 事件触发后继续执行当前线程
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public void publish(Remote remote,String host,int port) {
		String url = publishService(remote, host, port);//注册RMI，并返回RMI地址
		if (url != null) {
			ZooKeeper zk = connectServer();// 连接 ZooKeeper 服务器并获取 ZooKeeper 对象
			if (zk != null) {
				createNode(zk, url);// 创建 ZNode 并将 RMI 地址放入 ZNode 上
			}
		}
	}
	
	private String publishService(Remote remote,String host,int port) {
		String url = null;
		try {
			url = String.format("rmi://%s:%d/%s", host , port , remote.getClass().getName());
			LocateRegistry.createRegistry(port);
			Naming.rebind(url, remote);
			System.out.println(String.format("publish rmi service (url: %s)", url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	
	// 连接 ZooKeeper 服务器
	private ZooKeeper connectServer() {
		ZooKeeper zooKeeper = null;
		try {
			zooKeeper = new ZooKeeper(Constant.ZK_CONNECTION_STRING, Constant.ZK_SESSION_TIMEOUT, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						countDownLatch.countDown();// 唤醒当前正在执行的线程
					}
				}
			});
			
			countDownLatch.await();// 使当前线程处于等待状态
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zooKeeper;
	}
	
	 // 创建 ZNode
	private void createNode(ZooKeeper zk,String url) {
		try {
			byte[] data = url.getBytes();
			String path = zk.create(Constant.ZK_PROVIDER_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);// 创建一个临时性且有序的 ZNode
			System.out.println(String.format("create zookeeper node ({%s} => {%s})",path,url) );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
