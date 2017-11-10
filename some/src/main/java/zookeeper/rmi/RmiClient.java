package zookeeper.rmi;

import java.rmi.Naming;

public class RmiClient {
	public static void main(String[] args) throws Exception {
		String url = "rmi://172.20.69.31:9099/zookeeper.rmi.HelloServiceImpl";
		HelloService helloService = (HelloService) Naming.lookup(url);
		String result = helloService.sayHello("world1");
		System.out.println(result);
	}
}
