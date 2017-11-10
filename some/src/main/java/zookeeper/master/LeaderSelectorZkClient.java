package zookeeper.master;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by neil on 2017/11/8.
 */
public class LeaderSelectorZkClient {
    //启动的服务个数
    private static final int CLIENT_QTY = 10;
    //zookeeper服务器地址
    private static final String ZOOKEEPER_SERVER = "192.168.1.103:2181";

    public static void main(String[] args) throws Exception {
        List<ZkClient> zkClientList = new ArrayList<>();
        List<WorkServer> serverList = new ArrayList<>();

        try {
            for(int i=0;i<CLIENT_QTY;++i){
                //创建zkClient
                ZkClient zkClient = new ZkClient(ZOOKEEPER_SERVER,5000,5000,new SerializableSerializer());
                zkClientList.add(zkClient);
                //创建serverData
                RunningData serverData = new RunningData();
                serverData.setCid(Long.valueOf(i));
                serverData.setName("Client #" + i);
                WorkServer workServer = new WorkServer(serverData);
                workServer.setZkClient(zkClient);
                serverList.add(workServer);
                workServer.start();
            }
            System.out.println("敲回车键退出！\n");
           new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            System.out.println("Shutting down...");
            for(WorkServer server: serverList) {
                try {
                    server.stop();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(ZkClient zkClient:zkClientList) {
                try {
                    zkClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
