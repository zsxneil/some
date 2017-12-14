package com.my.service;

import com.my.model.Article;
import com.my.respo.ArticleSearchRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by neil on 2017/12/13.
 */
@Service
public class ArticleSearchService extends BaseSearchService<Article,Integer,ArticleSearchRepository> {

    public List<Article> findByTitle(String title){
        return getRepository().findByTitle(title);
    }


}
