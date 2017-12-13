package com.api.document;

import org.apache.lucene.search.highlight.Highlighter;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/12.
 */
public class SearchAPI {
    private TransportClient client;
    @Before
    public void getClient() throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));

    }

    @Test
    public void searchTest(){
        SearchResponse searchResponse = client.prepareSearch("schools")
                //.setTypes("tweet")
                .setQuery(QueryBuilders.matchQuery("name","SaintPaulSchool"))// Query 查询条件
                .setPostFilter(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("state","Delhi"))
                        .filter(QueryBuilders.rangeQuery("rating").from(2.0).to(5.5)) //范围过滤
                )
                .setFrom(0)
                //.setSize(2)
                .setExplain(true)
                .get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits){
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void searchMinTest(){
        SearchResponse searchResponse = client.prepareSearch("schools*").get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits){
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 虽然当滚动有效时间已过，搜索上下文(Search Context)会自动被清除，但是
     *一值保持滚动代价也是很大的，所以当我们不在使用滚动时要尽快使用Clear-
     *Scroll API进行清除。
     */
    @Test
    public void scrollResponse(){
        SearchResponse scrollResp = client.prepareSearch()
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(6000)) //为了使用 scroll，初始搜索请求应该在查询中指定 scroll 参数，告诉 Elasticsearch 需要保持搜索的上下文环境多长时间（滚动时间）
                .setFrom(0)
                .setSize(1)
                .get();

        //Scroll until no hits are returned
        do {
            System.out.println("scroll index plusing ...");
            for (SearchHit hit : scrollResp.getHits().getHits()){
                //Handle the hit...
                System.out.println(hit.getSourceAsString());
            }
            scrollResp = client
                    .prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(6000))
                    .execute()
                    .actionGet();
        } while (scrollResp.getHits().getHits().length != 0);// Zero hits mark the end of the scroll and the while loop.
    }

    @Test
    public void multiSearchTest()
    {
        SearchRequestBuilder srb1 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
        SearchRequestBuilder srb2 = client.prepareSearch().setQuery(QueryBuilders.matchQuery("user","kimchy")).setSize(2);

        MultiSearchResponse searchResponse = client.prepareMultiSearch()
                .add(srb1)
                .add(srb2)
                .get();

        long nHits = 0;
        for (MultiSearchResponse.Item item : searchResponse){
            SearchResponse response = item.getResponse();
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits){
                System.out.println(hit.getSourceAsString());
            }
            nHits += response.getHits().getTotalHits();
        }
        System.out.println(nHits);
    }

    /**
     * 测试已经使用ik分析器创建的索引查询，以及高亮显示
     */
    @Test
    public void ikTest(){
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("subject") //如果是所有字段，则使用 field("*")
                .requireFieldMatch(true);
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        SearchResponse searchResponse = client
        .prepareSearch("iktest")
        .setQuery(QueryBuilders.matchQuery("subject","希拉里和韩国"))
        .highlighter(highlightBuilder)
        .get();

        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits){
            List<String> highLights = new ArrayList<>();
            Map<String,HighlightField> highlightFields = hit.getHighlightFields();
            Map<String,Object> soucre = hit.getSource();
            //处理高亮 获取高亮字符串,如果有多个高亮字段，需要将（自己关心的）字段都遍历
            if (highlightFields != null && highlightFields.size() > 0) {
                HighlightField highlightField = highlightFields.get("subject");
                if (highlightField != null) {
                    Text[] fragments = highlightField.fragments();
                    if (fragments != null && fragments.length > 0) {
                        StringBuffer name = new StringBuffer();
                        for (Text text : fragments) {
                            name.append(text);
                        }
                        soucre.put("subject", name.toString());
                        highLights.add("subject:" + name.toString());
                    }
                }
            }
            System.out.println(soucre.toString());
        }
    }

    @After
    public void closeClient(){
        if (client != null){
            client.close();
        }
    }
}
