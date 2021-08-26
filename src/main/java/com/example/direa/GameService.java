package com.example.direa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {


    private final RestHighLevelClient client;





    public void create() throws IOException {
        String indexName = "xonmin_test_game";
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards",1)
                .put("index.number_of_replicas", 0));

        client.indices().create(request, RequestOptions.DEFAULT);
    }




    public void findByContent(String content) throws IOException {
        log.info("hi");
        SearchRequest searchRequest = new SearchRequest("xonmin_test_game");
        SearchSourceBuilder searchSourceBuilder =  new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("content",content));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();

        int numHits = (int) totalHits.value;

        log.info("total hit : {}",numHits);

        List<Game> gameList = new ArrayList<>();

        hits.forEach(hit -> {
            Map<String,Object> result = hit.getSourceAsMap();
            gameList.add(Game.builder()
                    .title(String.valueOf(result.get("title")))
                    .content(String.valueOf(result.get("content")))
                    .build());

        });
        gameList.forEach(game -> {
            System.out.println("이름 : " + game.getTitle());
            System.out.println("내용 : " + game.getContent());
        });

    }

    public void findByAll()throws IOException {
        SearchRequest searchRequest = new SearchRequest("xonmin_test_game");
        SearchSourceBuilder searchSourceBuilder =  new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();

        int numHits = (int) totalHits.value;

        log.info("total hit : {}",numHits);

        List<Game> gameList = new ArrayList<>();

        hits.forEach(hit -> {
            Map<String,Object> result = hit.getSourceAsMap();
            gameList.add(Game.builder()
                    .title(String.valueOf(result.get("title")))
                    .content(String.valueOf(result.get("content")))
                    .build());

        });
        gameList.forEach(game -> {
            System.out.println("이름 : " + game.getTitle());
            System.out.println("내용 : " + game.getContent());
        });


    }

}
