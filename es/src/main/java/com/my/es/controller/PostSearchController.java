package com.my.es.controller;

import com.my.es.model.Post;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

@RestController
@RequestMapping("/post/search")
public class PostSearchController {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;


    @GetMapping("singleWord")
    public List<Post> singleTitle(String word, @PageableDefault(sort = "weight", direction = Sort.Direction.DESC) Pageable pageable) {
        //使用queryStringQuery完成单字符串查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryStringQuery(word))
                .withPageable(pageable)
                .build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    @GetMapping("singleMatch")
    public List<Post> singleMatch(String content, @PageableDefault Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("content", content))
                .withPageable(pageable)
                .build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 单字段对某短语进行匹配查询，短语分词的顺序会影响结果
     */
    @RequestMapping("/singlePhraseMatch")
    public List<Post> singlePhraseMatch(String content, @PageableDefault Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchPhraseQuery("content", content))
                .withPageable(pageable)
                .build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 这个是最严格的匹配，属于低级查询，不进行分词的
     * 我们可以用这个来做那种需要==查询的操作，当传userId=1时，会查询出来所有userId为1的集合。
     * 通常情况下，我们不会使用term查询，绝大部分情况我们使用ES的目的就是为了使用它的分词模糊查询功能。
     * 而term一般适用于做过滤器filter的情况，譬如我们去查询title中包含“浣溪沙”且userId=1时，
     * 那么就可以用termQuery("userId", 1)作为查询的filter。

     * @param userId
     * @param pageable
     * @return
     */
    @RequestMapping("/singleTerm")
    public List<Post> singleTerm(Integer userId, @PageableDefault Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery("userId", userId))
                .withPageable(pageable)
                .build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 如果我们希望title，content两个字段去匹配某个字符串，只要任何一个字段包括该字符串即可，就可以使用multimatch。
     * @param title
     * @param pageable
     * @return
     */
    @RequestMapping("/multiMatch")
    public List<Post> multiMatch(String title, @PageableDefault Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(title, "title", "content"))
                .withPageable(pageable)
                .build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 之前的查询中，当我们输入“我天”时，ES会把分词后所有包含“我”和“天”的都查询出来，
     * 如果我们希望必须是包含了两个字的才能被查询出来，那么我们就需要设置一下Operator。
     *
     * 无论是matchQuery，multiMatchQuery，queryStringQuery等，都可以设置operator。默认为Or，
     * 置为And后，就会把符合包含所有输入的才查出来。
     * @param title
     * @return
     */
    @RequestMapping("/contain")
    public List<Post> contain(String title) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("title", title).operator(Operator.AND))
                .build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * boolQuery，可以设置多个条件的查询方式。它的作用是用来组合多个Query，有四种方式来组合，must，mustnot，filter，should。
     * must代表返回的文档必须满足must子句的条件，会参与计算分值；
     * filter代表返回的文档必须满足filter子句的条件，但不会参与计算分值；
     * should代表返回的文档可能满足should子句的条件，也可能不满足，有多个should时满足任何一个就可以，通过minimum_should_match设置至少满足几个。
     * mustnot代表必须不满足子句的条件。
     * @param title
     * @param userId
     * @param weight
     * @return
     */
    @RequestMapping("/bool")
    public List<Post> bool(String title, Integer userId, Integer weight) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        boolQuery()
                        .must(termQuery("userId", userId))
                        .should(rangeQuery("weight").lt(weight))
                        .mustNot(matchQuery("title", title))
                )
                .build();
        BoolQueryBuilder boolQueryBuilder = boolQuery();
        if (StringUtils.hasText(title)) {
            boolQueryBuilder.mustNot(matchQuery("title", title));
        }
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

}
