package com.example.direa.repository;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MetaRepository {

    private final RestHighLevelClient client;

    // meta data 전체 불러오기
    public SearchResponse findAllMetaData() throws IOException {
        //meta data index name =  label_index
        SearchRequest searchRequest = new SearchRequest("meta_index");
        SearchSourceBuilder searchSourceBuilder =  new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;
        }

    //텍스트 파일 이름으로 찾기
    public SearchResponse findMetadataByTxtFileName(String fileName) throws IOException {
        //meta data index name = meta_index
        SearchRequest searchRequest = new SearchRequest("meta_index");

        SearchSourceBuilder searchSourceBuilder =  new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("text_file_name",fileName));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;
    }


 }

