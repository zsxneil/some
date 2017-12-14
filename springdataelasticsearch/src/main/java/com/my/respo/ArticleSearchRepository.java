package com.my.respo;

import com.my.base.BaseSearchRepository;
import com.my.model.Article;

import java.util.List;


/**
 * Created by neil on 2017/12/13.
 */

public interface ArticleSearchRepository extends BaseSearchRepository<Article,Integer> {
    List<Article> findByTitle(String title);
}
