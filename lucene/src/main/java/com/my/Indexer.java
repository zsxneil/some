package com.my;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by neil on 2017/11/30.
 */
public class Indexer {

    /** 建立索引
     发表过文章过后，不仅数据库中有存储记录 索引库中也必须有一条*/
    public static void main(String[] args) throws IOException {

        // 模拟一条数据库中的记录
        Article article = new Article(1,"Lucene全文检索框架",
                "Lucene如果信息检索系统在用户发出了检索请求后再去网上找答案","Neil");

        // 建立索引
        // 1、把Article转换为Doucement对象
        Document document = new Document();
        //根据实际情况，使用不同的Field来对原始内容建立索引， Store.YES表示是否存储字段原始内容
        document.add(new LongField("id",article.getId(), Field.Store.YES));
        document.add(new TextField("title",article.getTitle(), Field.Store.YES));
        document.add(new TextField("content",article.getContent(), Field.Store.YES));
        document.add(new StringField("author",article.getAuthor(), Field.Store.YES));

        // 2、建立索引
        // 指定索引库的位置，本例为项目根目录下indexDir
        Directory directory = FSDirectory.open(new File("./indexDir/"));
        // 分词器，不同的分词器有不同的规则
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST,analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,writerConfig);
        indexWriter.addDocument(document);
        indexWriter.close();
    }
}
