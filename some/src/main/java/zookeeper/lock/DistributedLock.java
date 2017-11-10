package zookeeper.lock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * Zookeeper中有一种节点叫做顺序节点，故名思议，假如我们在/lock/目录下创建节3个点，ZooKeeper集群会按照提起创建的顺序来创建节点，节点分别为/lock/0000000001、/lock/0000000002、/lock/0000000003。
 * ZooKeeper中还有一种名为临时节点的节点，临时节点由某个客户端创建，当客户端与ZooKeeper集群断开连接，。则该节点自动被删除。
 * 算法思路：对于加锁操作，可以让所有客户端都去/lock目录下创建临时、顺序节点，
 * 	如果创建的客户端发现自身创建节点序列号是/lock/目录下最小的节点，则获得锁。
 * 否则，监视比自己创建节点的序列号小的节点（当前序列在自己前面一个的节点），进入等待。
 * 解锁操作，只需要将自身创建的节点删除即可
 * @author Administrator
 *
 */
public class DistributedLock implements Lock, Watcher {

	private ZooKeeper zooKeeper = null;
	private String root = "/locks";//根
	private String lockName;//竞争资源的标识
	private String waitNode;//等待前一个锁
	private String myNode;	//当前锁
	private CountDownLatch latch;//计数器
	private int sessionTimeout = 30000;//
	private List<Exception> exception = new ArrayList<>();
	
	/**
     * 创建分布式锁,使用前请确认config配置的zookeeper服务可用
     * @param config 127.0.0.1:2181
     * @param lockName 竞争资源标志,lockName中不能包含单词lock
     */
	public DistributedLock(String config,String lockName) {
		this.lockName = lockName;
		// 创建一个与服务器的连接
		try {
			zooKeeper = new ZooKeeper(config, sessionTimeout, this);
			Stat stat = zooKeeper.exists(root, false);
			if (stat == null) {
				// 创建根节点
				zooKeeper.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (IOException e) {
			exception.add(e);
			e.printStackTrace();
		} catch (KeeperException e) {
			exception.add(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			exception.add(e);
			e.printStackTrace();
		}
		
	}

	/**
     * zookeeper节点的监视器
     */
	@Override
	public void process(WatchedEvent event) {
		if (this.latch != null) {
			this.latch.countDown();
		}
	}

	@Override
	public void lock() {
		if (exception.size() > 0) {
			throw new LockException(exception.get(0));
		}
		try {
			if (this.tryLock()) {
				System.out.println("Thread " + Thread.currentThread().getId() + " " + myNode + " get lock true");
				return;
			} else {
				waitForLock(waitNode, sessionTimeout);//等待锁
			}
		} 
		catch (InterruptedException e) {
			throw new LockException(e);
		} catch (KeeperException e) {
			throw new LockException(e);
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		try {
			String splitStr = "_lock_";
			if (lockName.contains(splitStr)) { //不能包含lock，避免误判
				throw new LockException("lockName can not contains \\u000B");
			}
			//创建临时子节点
			myNode = zooKeeper.create(root + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println(myNode + " is created");
			//取出所有子节点
			List<String> subNodes = zooKeeper.getChildren(root, false);
			//取出所有lockName的锁
			List<String> lockObjNodes = new ArrayList<>();
			for (String node : subNodes) {
				String _node = node.split(splitStr)[0];
				if (_node.equals(lockName)) {
					lockObjNodes.add(node);
				}
			}
			Collections.sort(lockObjNodes);
			System.out.println(myNode + "==" + lockObjNodes.get(0));
			if (myNode.equals(root + "/" + lockObjNodes.get(0))) {
				//如果是最小的节点,则表示取得锁
                return true;
			}
			//如果不是最小的节点，找到比自己小1的节点
			String subMyNode = myNode.substring(myNode.lastIndexOf("/") + 1);
			waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyNode) - 1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		try {
			if (this.tryLock()) {
				return true;
			}
			return waitForLock(waitNode, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void unlock() {
		try {
			System.out.println("unlock " + myNode);
			zooKeeper.delete(myNode, -1);
			myNode = null;
			zooKeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean waitForLock(String lower, long waitTime) throws KeeperException, InterruptedException {
		Stat stat = zooKeeper.exists(root + "/" + lower, true);
		if (stat != null) {
			//判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
			System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + root + "/" + lower);
			this.latch = new CountDownLatch(1);
			//this.latch.await(waitTime, TimeUnit.MILLISECONDS);
			this.latch.await();
			this.latch = null;
			//System.out.println(lower + " release lock");
		}
		return true;
	}

	public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public LockException(String e){
            super(e);
        }
        public LockException(Exception e){
            super(e);
        }
    }
}
