package com.my;

import com.my.model.Article;
import com.my.service.ArticleSearchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */
public class App {

    ArticleSearchService searchService;
    @Before
    public void init(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring.xml","classpath:spring-elasticsearch.xml"});
        searchService = (ArticleSearchService) applicationContext.getBean("articleSearchService");
    }

    @Test
    public void test() throws IOException {
        findByTitle();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        findByTitleAndContent();
    }

    @Test
    public void save(){

        Article article = new Article();
        article.setId(3);
        article.setTitle("这是第三个测试啊");
        article.setContent("希拉里团队炮轰FBI 参院民主党领袖批其“违法”");
        article.setReleaseDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        searchService.save(article);
    }

    @Test
    public void findByTitle(){
        List<Article> articles = searchService.findByTitle("测试");
        for (Article article : articles) {
            System.out.println(article.toString());
        }
    }

    @Test
    public void findByTitleAndContent() throws IOException {
        List<Article> articles = searchService.findByTitleAndContent("韩国测试希拉里");
        for (Article article : articles) {
            System.out.println(article.toString());
        }
    }

    @After
    public void close(){
        searchService.getElasticsearchTemplate().getClient().close();
    }
}
