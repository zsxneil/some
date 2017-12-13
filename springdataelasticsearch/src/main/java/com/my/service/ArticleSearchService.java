package com.my.service;

import com.my.model.Article;
import com.my.respo.ArticleSearcjRepository;

import java.util.List;

/**
 * Created by neil on 2017/12/13.
 */
public class ArticleSearchService extends BaseSearchService<Article,Integer,ArticleSearcjRepository> {
    public List<Article> findByTitle(String title){
        return getRepository().findByTitle(title);
    }
}
