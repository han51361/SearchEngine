package com.example.direa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class GameProvider implements GameUseCase {

    @Autowired
    private final RestHighLevelClient restHighLevelClient;

    public GameProvider(RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;

    }
    @Override
    public Mono<Void> addDocument(Game game)  {
        IndexRequest indexRequest = new IndexRequest("xonmin_test_game")
                .source("title", game.getTitle(),
                        "content", game.getContent());

        return Mono.create(sink -> {
            ActionListener<IndexResponse> actionListener = new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {
                    sink.success(); }
                @Override
                public void onFailure(Exception e) {
                }
            };
            restHighLevelClient.indexAsync(indexRequest, RequestOptions.DEFAULT,actionListener);
        });
    }

    @Override
    public Flux<Game> searchByMultiQuery(String content) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("content",content));
        log.info("searchquery generate");
        return getGameFlux(searchSourceBuilder);
    }

    private Flux<Game> getGameFlux(SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest("xonmin_test_game");
        searchRequest.source(searchSourceBuilder);

        return Flux.<Game>create(sink -> {
            ActionListener<SearchResponse> actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {

                    for(SearchHit hit : searchResponse.getHits()) {
                        ObjectMapper objectMapper =  new ObjectMapper();
                        try {
                            Game game =  objectMapper.readValue(hit.getSourceAsString(), Game.class);
                            sink.next(game);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    sink.complete();
                }

                @Override
                public void onFailure(Exception e) {

                }
            };
            restHighLevelClient.searchAsync(searchRequest,RequestOptions.DEFAULT,actionListener);
        });
    }

    public List<Game> testGetsearch(String content) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("content",content));

        log.info("error",QueryBuilders.termQuery("content",content));

        SearchRequest searchRequest = new SearchRequest("xonmin_test_game");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();


        List<Game> gameList = new ArrayList<>();

        hits.forEach(hit ->{
            Map<String,Object> result =  hit.getSourceAsMap();



            gameList.add(Game.builder()
                    .title(String.valueOf(result.get("title")))
                    .content(String.valueOf(result.get("content")))
                    .build());
        });
        return gameList;
    }

}
