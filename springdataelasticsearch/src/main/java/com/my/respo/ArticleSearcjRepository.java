package com.my.respo;

import com.my.model.Article;

import java.util.List;

/**
 * Created by neil on 2017/12/13.
 */
public interface ArticleSearcjRepository extends BaseSearchRepository<Article,Integer> {
    List<Article> findByTitle(String title);
}
