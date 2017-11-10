package zookeeper;

import java.rmi.RemoteException;

import zookeeper.rmi.HelloService;

public class Client {
	public static void main(String[] args) throws RemoteException, InterruptedException {
		ServiceConsumer consumer = new ServiceConsumer();
		while (true) {
			HelloService helloService = consumer.lookup();
			String result = helloService.sayHello("Jack");
			System.out.println(result);
			Thread.sleep(3000);
		}
	}
}
