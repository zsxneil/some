package com.api.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/11.
 */
public class IndexAPI {
    private TransportClient client;

    @Before
    public void getClient() throws UnknownHostException {
        //设置集群名称
        //Settings settings = Settings.builder().put("cluster.name","my-application").build();
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
    }

    /**
     * 手动生成JSON
     */
    @Test
    public void createJson(){
        String json = "{" +
                "\"user\":\"fendo\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"Hell word\"" +
                "}";

        IndexResponse response = client
                .prepareIndex("fendo","fendodate")
                .setSource(json)
                .get();
        System.out.println(response.getResult());
    }

    /**
     * 使用集合
     */
    @Test
    public void createList(){
        Map<String,Object> json = new HashMap<>();
        json.put("user","kimchy");
        json.put("postDate",new Date());
        json.put("message", "trying out ElasticSearch");
        IndexResponse response = client.prepareIndex("fendo","fendodate").setSource(json).get();
        System.out.println(response.getResult());
    }

    /**
     * 使用jackson
     */
    @Test
    public void createJackson() throws JsonProcessingException {
        CsdnBlog blog = new CsdnBlog();
        blog.setAuthor("fendo");
        blog.setContent("this is java book");
        blog.setTag("C");
        blog.setView("100");
        blog.setTitle("program");
        blog.setDate(new Date().toString());

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        byte[] json = mapper.writeValueAsBytes(blog);
        IndexResponse response = client.prepareIndex("fendo", "fendodate").setSource(json).get();
        System.out.println(response.getResult());

    }

    /**
     * 使用ElasticSearch 帮助类
     * @throws IOException
     */
    @Test
    public void createXContentBuilder() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("user","case")
                .field("postDate",new Date())
                .field("message","this is elasticSearch")
                .endObject();

        IndexResponse response = client.prepareIndex("fendo","fendodate").setSource(builder).get();
        System.out.println(response.getResult());
    }

    @After
    public void closeClient(){
        if (client != null){
            client.close();
        }
    }
}
