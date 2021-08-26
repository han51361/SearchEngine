package com.example.direa.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QueryRepository {

    private final RestHighLevelClient restHighLevelClient;

    // 오탈자교정
    public SearchResponse findSpellCheckBySuggester(String input) throws IOException {
        //spell_check_index

        SearchSourceBuilder searchSourceBuilder =  new SearchSourceBuilder();
        SuggestionBuilder<TermSuggestionBuilder> termsSuggestionBuilder = SuggestBuilders.termSuggestion("title.spell").text(input);
        SuggestBuilder suggestBuilder =  new SuggestBuilder();

        suggestBuilder.addSuggestion("my_suggester",termsSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);

        SearchRequest searchRequest =  new SearchRequest("spell_check_index");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;
    }

    //동의어 확인
    public SearchResponse findSynonymByMatch(String input) throws IOException{
        SearchRequest searchRequest = new SearchRequest("synonym_index");

        SearchSourceBuilder searchSourceBuilder =  new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("word",input));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponse;
    }

    //검색어 txtindex로 쿼리
    public SearchResponse findTextDataByBool(String input, int page) throws IOException {
        SearchRequest searchRequest = new SearchRequest("d_search_test_1");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("core.contents","*"+input+"*"))
        //.should(QueryBuilders.matchQuery("contents",synonym))
        );

        HighlightBuilder highlightBuilder = new HighlightBuilder().field(input).preTags("<em>").postTags("</em>");
        searchSourceBuilder.from(10*(page-1)).size(10).highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        log.info("{}",searchSourceBuilder);
        SearchResponse searchResponse =  restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        log.info("리포지토리 반환 값",searchResponse);
        System.out.println(searchResponse);
        return searchResponse;
    }
}
