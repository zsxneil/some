package com.my.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.my.model.Article;
import com.my.respo.ArticleSearchRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by neil on 2017/12/13.
 */
@Service
public class ArticleSearchService extends BaseSearchService<Article,Integer,ArticleSearchRepository> {

    public List<Article> findByTitle(String title){
        return getRepository().findByTitle(title);
    }

    public List<Article> findByTitleAndContent(String words) throws IOException {
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("*")
                .requireFieldMatch(true);
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        String[] highLightFields = new String[]{"title","content"};

        SearchResponse searchResponse = getElasticsearchTemplate()
                                            .getClient()
                                            .prepareSearch("article_index")
                                            .setQuery(QueryBuilders.multiMatchQuery(words,highLightFields))
                                            .highlighter(highlightBuilder)
                                            .get();
        SearchHits hits = searchResponse.getHits();

        List<Article> articleList = new ArrayList<>();
        for (SearchHit hit : hits){
            Map<String,HighlightField> highlightFieldMap = hit.getHighlightFields();
            Map<String,Object> source = hit.getSource();
            if (highlightFieldMap != null && highlightFieldMap.size() > 0){
                for (String field : highLightFields) {
                    HighlightField highlightField = highlightFieldMap.get(field);
                    if (highlightField != null) {
                        Text[] fragment = highlightField.fragments();
                        if (fragment != null && fragment.length > 0){
                            StringBuffer buffer = new StringBuffer();
                            for (Text text : fragment) {
                                buffer.append(text.toString());
                            }
                            source.put(field, buffer.toString());
                        }

                    }
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            //mapper.readValue(source, Article.class);
            Article article = mapper.readValue(JSONObject.toJSONString(source),Article.class);
            articleList.add(article);
        }
        getElasticsearchTemplate().getClient().close();
        return articleList;
    }

}
