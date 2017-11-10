package zookeeper;

public interface Constant {
	String ZK_CONNECTION_STRING = "172.20.183.137:2181,172.20.183.137:2182,172.20.183.137:2183";
    int ZK_SESSION_TIMEOUT = 5000;
    String ZK_REGISTRY_PATH = "/registry";//须在zookeeper中先创建
    String ZK_PROVIDER_PATH = ZK_REGISTRY_PATH + "/provider" ;
}
