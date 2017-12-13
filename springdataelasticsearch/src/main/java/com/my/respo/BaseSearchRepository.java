package com.my.respo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * Created by neil on 2017/12/13.
 */
public interface BaseSearchRepository<E,ID extends Serializable> extends ElasticsearchRepository<E,ID> , PagingAndSortingRepository<E,ID>{

}
