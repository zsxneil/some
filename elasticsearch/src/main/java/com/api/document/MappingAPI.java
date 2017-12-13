package com.api.document;

import com.api.ClientBase;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/12/13.
 */
public class MappingAPI extends ClientBase{

    /**
     * 在mapping配置每个字段是否用ik中文分析器，可以提高效率
     */
    @Test
    public void mappingTest(){

        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory
                    .jsonBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("title")
                                .field("type","string")
                                .field("analyzer","ik_max_word")
                            .endObject()
                            .startObject("question")
                                .field("type","string")
                                .field("analyzer","ik_max_word")
                            .endObject()
                            .startObject("answer")
                                .field("type","string")
                                .field("analyzer","ik_max_word")
                            .endObject()
                            .startObject("category")
                                .field("type","string")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("author")
                                .field("type","string")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("date")
                                .field("type","string")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("answer_author")
                                .field("type","string")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("answer_date")
                                .field("type","string")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("description")
                                .field("type","string")
                                .field("analyzer","ik_max_word")
                            .endObject()
                            .startObject("keywords")
                                .field("type","string")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("read_count")
                                .field("type","integer")
                                .field("index","not_analyzed")
                            .endObject()
                            //关联数据
                            .startObject("list").field("type","object").endObject()
                        .endObject()
                    .endObject();

            CreateIndexRequest createIndexRequest = new CreateIndexRequest("index");
            CreateIndexResponse response1 = client.admin().indices().create(createIndexRequest).actionGet(); //先创建一个空索引
            System.out.println(response1.isAcknowledged());

            PutMappingRequest mappingRequest = Requests.putMappingRequest("index").type("type").source(mapping);
            PutMappingResponse response = client.admin().indices().putMapping(mappingRequest).get();
            System.out.println(response.toString() + "   ;" + response.isAcknowledged());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
