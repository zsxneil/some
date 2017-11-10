package zookeeper;

import java.rmi.RemoteException;

import zookeeper.rmi.HelloService;
import zookeeper.rmi.HelloServiceImpl;

public class Server {
	public static void main(String[] args) throws RemoteException, InterruptedException {
		String host = "localhost";
		int port = 9098;
		
		ServiceProvider provider = new ServiceProvider();
		HelloService service = new HelloServiceImpl();
		provider.publish(service, host, port);
		
		Thread.sleep(Long.MAX_VALUE);
	}
}
