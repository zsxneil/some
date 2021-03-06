package com.my;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neil on 2017/12/2.
 */
public class Searcher {
    public static void main(String[] args) throws IOException, ParseException {
        //搜索条件（不区分大小写）
        String queryString = "检索";
        //String queryString = "lucene";

        // 进行搜索得到结果
        // ==============================
        Directory directory = FSDirectory.open(new File("./indexDir/"));
        Analyzer analyzer = new StandardAnalyzer();

        // 1、把查询字符串转为查询对象(存储的都是二进制文件，普通的String肯定无法查询，因此需要转换)
        QueryParser queryParser = new QueryParser("title", analyzer);//只在标题里查询
        Query query = queryParser.parse(queryString);


        // 2、查询，得到中间结果
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs topDocs = indexSearcher.search(query, 100);// 根据指定查询条件查询，只返回前n条结果
        int count = topDocs.totalHits;//总结果数
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;//按照得分排序后的前n条结果的信息
        List<Article> articleList = new ArrayList<Article>();

        //3.处理中间结果
        for (ScoreDoc scoreDoc : scoreDocs) {
            float score = scoreDoc.score;//相关度得分
            int docId = scoreDoc.doc;//Document在数据库的内部编号(是唯一的，由lucene自动生成)

            //根据编号取出真正的Document数据
            Document document = indexSearcher.doc(docId);
            // 把Document转成Article
            Article article = new Article(Integer.parseInt(document.getField("id").stringValue()),//需要转换为int型
                    document.getField("title").stringValue(),
                    document.getField("content").stringValue(),
                    document.getField("author").stringValue());

            articleList.add(article);

        }
        indexReader.close();
        // ============查询结束====================

        //显示结果
        System.out.println("总结果数量为：" + articleList.size());
        for (Article article : articleList) {
            System.out.println(article);
        }
    }
}
