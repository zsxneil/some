package com.api;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/12/13.
 */
public class ClientBase {
    public TransportClient client;
    @Before
    public void getClient() throws UnknownHostException {
        //设置集群名称
        //Settings settings = Settings.builder().put("cluster.name","my-application").build();
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
    }

    @After
    public void closeClient(){
        if (client != null){
            client.close();
        }
    }
}
