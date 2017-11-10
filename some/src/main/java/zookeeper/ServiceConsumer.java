package zookeeper;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ServiceConsumer {
	
	private static final Logger log = Logger.getLogger(ServiceProvider.class);
	
	// 用于等待 SyncConnected 事件触发后继续执行当前线程
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	// 定义一个 volatile 成员变量，用于保存最新的 RMI 地址（考虑到该变量或许会被其它线程所修改，一旦修改后，该变量的值会影响到所有线程）
	private volatile List<String> urlList = new ArrayList<>();

	public ServiceConsumer() {
		ZooKeeper zk = connectServer();
		if (zk != null) {
			watchNode(zk);
		}
	}
	
	//查找RMI服务
	public <T extends Remote> T lookup() {
		T service = null;
		int size = urlList.size();
		if (size > 0) {
			String url;
			if (size == 1) {
				url = urlList.get(0);// 若 urlList 中只有一个元素，则直接获取该元素
				System.out.println(String.format("using only url: %s", url));
			} else {
				url = urlList.get(ThreadLocalRandom.current().nextInt(size));// 若 urlList 中存在多个元素，则随机获取一个元素
				System.out.println(String.format("using random url: %s", url));
			}
			service = lookupService( url); // 从 JNDI 中查找 RMI 服务
		}
		return service;
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
	
	 // 观察 /registry 节点下所有子节点是否有变化
	private void watchNode(final ZooKeeper zk) {
		try {
			List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						watchNode(zk); // 若子节点有变化，则重新调用该方法（为了获取最新子节点中的数据）
					}
				}
			});
			
			List<String> dataList = new ArrayList<>();
			for(String node : nodeList) {
				byte[] data = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
				dataList.add(new String(data));
			}
			System.out.println(String.format("node data: %s" , dataList));
			urlList = dataList;// 更新最新的 RMI 地址
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 在 JNDI 中查找 RMI 远程服务对象
	@SuppressWarnings("unchecked")
	private <T> T lookupService(String url){
		T remote = null;
		try {
			remote = (T) Naming.lookup(url);
		} catch (Exception e) {
			if (e instanceof ConnectException) {
				// 若连接中断，则使用 urlList 中第一个 RMI 地址来查找（这是一种简单的重试方式，确保不会抛出异常）
				System.out.println(String.format("ConnectException -> url: %s", url));
				if (urlList.size() != 0) {
					url = urlList.get(0);
					return lookupService(url);
				}
			}
			e.printStackTrace();
		}
		return remote;
	}
}
