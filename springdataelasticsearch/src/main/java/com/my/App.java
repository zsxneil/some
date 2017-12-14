package com.my;

import com.my.model.Article;
import com.my.service.ArticleSearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */
public class App {
    private ApplicationContext applicationContext;

    @Before
    public void init(){
        applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring.xml","classpath:spring-elasticsearch.xml"});
    }

    @Test
    public void save(){
        ArticleSearchService searchService = (ArticleSearchService) applicationContext.getBean("articleSearchService");
        Article article = new Article();
        article.setId(2);
        article.setTitle("这是第二个测试啊");
        article.setContent("希拉里团队炮轰FBI 参院民主党领袖批其“违法”");
        article.setReleaseDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        searchService.save(article);
    }

    @Test
    public void findByTitle(){
        ArticleSearchService searchService = (ArticleSearchService) applicationContext.getBean("articleSearchService");
        List<Article> articles = searchService.findByTitle("测试");
        for (Article article : articles) {
            System.out.println(article.toString());
        }
    }
}
