package com.my.service;

import com.my.base.BaseSearchRepository;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by neil on 2017/12/13.
 */

public abstract class BaseSearchService<E,ID extends Serializable,R extends BaseSearchRepository<E,ID>> {
    private  Logger logger = Logger.getLogger(this.getClass());

    private R repository;

    public R getRepository() {
        return repository;
    }

    public ElasticsearchTemplate getElasticsearchTemplate() {
        return elasticsearchTemplate;
    }

    public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    public Optional<E> getById(ID id){
        return repository.findById(id);
    }

    public Iterable<E> listAll(){
        return repository.findAll();
    }

    public void save(E e){
        repository.save(e);
    }

    public void delete(E e){
        repository.delete(e);
    }

    public void deleteById(ID id){
        repository.deleteById(id);
    }

    public E getByKey(String fieldName,Object value){
        return repository.search(QueryBuilders.matchQuery(fieldName,value)).iterator().next();
    }

}
