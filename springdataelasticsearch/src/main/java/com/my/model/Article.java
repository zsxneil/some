package com.my.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/13.
 */
@Document(indexName = "article_index",type = "article_type")
@Setting(settingPath = "article_analyzer.json")
public class Article implements Serializable {
    @Id
    private Integer id;
    @Field(type = FieldType.text,analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.text,analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Date,store = true,format = DateFormat.custom,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private String releaseDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
