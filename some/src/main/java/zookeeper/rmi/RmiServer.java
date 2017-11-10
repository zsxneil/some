package zookeeper.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RmiServer {
	public static void main(String[] args) throws Exception {
		int port = 9099;
		String url = "rmi://localhost:9099/zookeeper.rmi.HelloServiceImpl";
		LocateRegistry.createRegistry(port);
		Naming.rebind(url, new HelloServiceImpl());
	}
}
