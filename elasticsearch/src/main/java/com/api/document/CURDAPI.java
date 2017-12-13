package com.api.document;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/12/12.
 */
public class CURDAPI {
    private TransportClient client;

    @Before
    public void getClient() throws UnknownHostException {
        //设置集群名称
        Settings settings = Settings.builder().put("cluster.name","my-application").build();
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
    }

    @Test
    public void getTest(){
        GetResponse response = client.prepareGet("fendo","fendodate","AWBE0TB8bAFAigJpxvkc") //根据id获取内容
                .setOperationThreaded(false)
                .get();
        if (response.isExists()){ //存在返回true
            System.out.println(response.getSourceAsString());
            Map<String,GetField> fields = response.getFields();
            Set<String> keySet = fields.keySet();
            System.out.println("**********fields************");//不知道这个是什么
            for (String key : keySet) {
                System.out.println(key + " : " + fields.get(key));
            }

            System.out.println("***********source*************");
            Map<String,Object> source = response.getSource();
            Set<String> fieldSet = source.keySet();
            for(String field : fieldSet) {
                System.out.println(field + " : " + source.get(field));
            }
        }

    }

    @Test
    public void deleteTest(){
        DeleteResponse response = client.prepareDelete("fendo","fendodate","AWBE0TB8bAFAigJpxvkc")
                .get();
        System.out.println("rest状态码：" + response.status());//如果不存在返回“NOT_FOUND”,存在且执行成功，返回“OK”
        System.out.println(response.getResult());//成功删除，返回“DELETED”
    }

    @Test
    public void deleteByQueryTest(){
        BulkIndexByScrollResponse response =
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchPhraseQuery("user","case")) //查询条件
                .source("fendo") //index(索引名) //不存在时：IndexNotFoundException
                .get();
        System.out.println("删除的数量" + response.getDeleted());
    }

    @Test
    public void deleteByQueryAsyncTest() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("user","fendo"))
                .source("fendo")
                .execute(new ActionListener<BulkIndexByScrollResponse>() { //回调监听
                    @Override
                    public void onResponse(BulkIndexByScrollResponse bulkIndexByScrollResponse) {
                        System.out.println("删除文档的数量：" + bulkIndexByScrollResponse.getDeleted());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
        try {
            Thread.sleep(5000); //异步回调，需要注意连接的关闭时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateRequest() throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("fendo")
                .type("fendodate")
                .id("AWBErjzfbAFAigJpxvkY")
                .doc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user","kimchy1")
                        .endObject());
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.status());
    }

    @Test
    public void updatePrepareTest() throws IOException {
        UpdateResponse updateResponse =
        client.prepareUpdate("fendo","fendodate","AWBErjzfbAFAigJpxvkY")
                .setDoc(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("user","kimchy2")
                            .endObject())
                .get();
        System.out.println(updateResponse.status());
    }

    @Test
    public void updateByScript() throws ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest("fendo","fendodate","AWBErjzfbAFAigJpxvkY")
                .script(new Script("ctx._source.user=\"kimchy3\""));
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.status());
    }

    /**
     * 更新插入：如果存在就更新，不存在就插入
     */
    @Test
    public void upsert() throws IOException, ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest("fendo","fendodate","1")
                .source(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user","Joe Smith")
                        .field("gender","male")
                        .endObject());

        UpdateRequest updateRequest = new UpdateRequest("fendo","fendodate","1")
                .doc(XContentFactory
                        .jsonBuilder()
                        .startObject()
                        .field("gender","female")
                        .endObject()).upsert(indexRequest);
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.status());
    }

    @Test
    public void multiGetTest(){
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("fendo","fendodate","1")
                .add("iktest","article","1","2","3")
                .get();
        for(MultiGetItemResponse getItemResponse : multiGetItemResponses) {
            GetResponse getResponse = getItemResponse.getResponse();
            if (getResponse.isExists()) {   //判断是否存在
                System.out.println(getResponse.getSourceAsString());
            }
        }
    }

    @Test
    public void bulkTest() throws IOException {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        bulkRequestBuilder.add(
                client.prepareIndex("twitter","tweet","1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user","kimchy")
                        .field("postDate",new Date())
                        .field("message","trying out ElasticSearch")
                        .endObject())
        );
        bulkRequestBuilder.add(client.prepareIndex("twitter","tweet","2")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user","kimchy")
                        .field("postDate",new Date())
                        .field("message","another post")
                        .endObject())
        );

        BulkResponse bulkResponse = bulkRequestBuilder.get();
        for (BulkItemResponse bulkItemResponse : bulkResponse){
            IndexResponse response = bulkItemResponse.getResponse();
            System.out.println(response.getResult());
        }

    }

    @After
    public void closeClient(){
        if (client != null){
            client.close();
        }
    }
}
