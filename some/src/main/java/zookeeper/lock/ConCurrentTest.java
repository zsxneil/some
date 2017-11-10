package zookeeper.lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class ConCurrentTest {
	private CountDownLatch startSignal = new CountDownLatch(1);//开始阀门
	private CountDownLatch doneSignal = null;//结束阀门
	private CopyOnWriteArrayList<Long> list = new CopyOnWriteArrayList<>();
	private AtomicLong err = new AtomicLong();//原子递增
	private ConcurrentTask[] task = null;
	
	public ConCurrentTest(ConcurrentTask... task) {
		this.task = task;
		if (task == null) {
			System.out.println("task can not null");
            System.exit(1);
		}
		doneSignal = new CountDownLatch(task.length);
		start();
	}

	private void start() {
		//创建线程，并将所有线程等待在阀门处
		createThread();
		//打开阀门
		startSignal.countDown();//递减锁存器的计数，如果计数到达零，则释放所有等待的线程
		try {
			doneSignal.await();//等待所有线程都执行完毕
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//计算执行时间
		getExeTime();
	}

	/**
     * 计算平均响应时间
     */
	private void getExeTime() {
		int size = list.size();
		List<Long> _list = new ArrayList<>();
		_list.addAll(list);
		Collections.sort(_list);
		long min = _list.get(0);
		long max = _list.get(size - 1);
		long sum = 0;
		for (long time : _list) {
			sum += time;
		}
		long avg = sum/size;
        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("avg: " + avg);
        System.out.println("err: " + err.get());
	}

	/**
     * 初始化所有线程，并在阀门处等待
     */
	private void createThread() {
		long len = doneSignal.getCount();
		for(int i=0;i<len;i++){
			final int j = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						startSignal.await();
						long start = System.currentTimeMillis();
						task[j].run();
						long end = System.currentTimeMillis() - start;
						list.add(end);
					} catch (InterruptedException e) {
						e.printStackTrace();
						err.getAndIncrement();
					}
					doneSignal.countDown();
				}
			}).start();
		}
	}

	public interface ConcurrentTask {
        void run();
    }
}
