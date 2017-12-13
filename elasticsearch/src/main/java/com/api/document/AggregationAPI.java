package com.api.document;

import org.apache.lucene.index.Terms;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 聚合框架有助于根据搜索查询提供聚合数据。它是基于简单的构建块也称为整
 *合，整合就是将复杂的数据摘要有序的放在一块。
 *聚合可以被看做是从一组文件中获取分析信息的一系列工作的统称。聚合的实
 *现过程就是定义这个文档集的过程（例如，在搜索请求的基础上，执行查询/过
 *滤，才能得到高水平的聚合结果）。
 * Created by Administrator on 2017/12/12.
 */
public class AggregationAPI {

    private TransportClient client = null;

    @Before
    public void getClient() throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));

    }

    /**
     * 没搞懂，有点复杂
     * @throws IOException
     */
    @Test
    public void aggregation() throws IOException {
        SearchResponse response = client.prepareSearch()
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("agg1").field("field"))
                .addAggregation(AggregationBuilders.dateHistogram("agg2").field("birth").dateHistogramInterval(DateHistogramInterval.YEAR))
                .get();

        Terms agg1 = response.getAggregations().get("agg1");
        Histogram agg2 = response.getAggregations().get("agg2");
        System.out.println(agg1.getDocCount() + " /" + agg2.getName());
    }

    @Test
    public void max(){

        MaxAggregationBuilder aggregation =
                AggregationBuilders
                        .max("agg")
                        .field("fees");
        SearchResponse response = client.prepareSearch()
                .addAggregation(aggregation)
                .get();
        Max max = response.getAggregations().get("agg");
        double value = max.getValue();
        System.out.println(value);

    }

    @Test
    public void min(){
        MinAggregationBuilder aggregation = AggregationBuilders.min("agg").field("fees");
        SearchResponse response = client.prepareSearch().addAggregation(aggregation).get();
        Min min = response.getAggregations().get("agg");
        double value = min.getValue();
        System.out.println(value);
    }

    @Test
    public void sum(){
        SumAggregationBuilder aggregation = AggregationBuilders.sum("agg").field("fees");
        SearchResponse response = client.prepareSearch().addAggregation(aggregation).get();
        Sum sum = response.getAggregations().get("agg");
        double value = sum.getValue();
        System.out.println(value);
    }

    @Test
    public void avg(){
        AvgAggregationBuilder aggregation = AggregationBuilders.avg("agg").field("fees");
        SearchResponse response = client.prepareSearch().addAggregation(aggregation).get();
        Avg avg = response.getAggregations().get("agg");
        double value = avg.getValue();
        System.out.println(value);
    }

    /**
     * 统计聚合——基于文档的某个值，计算出一些统计信息（min、max、sum、
     *count、avg）, 用于计算的值可以是特定的数值型字段，也可以通过脚本计算
     *而来。
     */
    @Test
    public void stats(){
        StatsAggregationBuilder aggregation = AggregationBuilders.stats("agg").field("fees");
        SearchResponse response = client.prepareSearch().addAggregation(aggregation).get();
        Stats stats = response.getAggregations().get("agg");
        System.out.println("max:" + stats.getMax() + "\nmin:" + stats.getMin() + "\navg:" + stats.getAvg() + "\nsum:" + stats.getSumAsString() + "\ncount:" + stats.getCount());
    }




    @After
    public void closeClient(){
        if (client != null){
            client.close();
        }
    }
}
