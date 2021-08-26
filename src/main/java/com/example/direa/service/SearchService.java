package com.example.direa.service;


import com.example.direa.data.Dsearch;
import com.example.direa.data.MetaDataIndex;
import com.example.direa.repository.MetaRepository;
import com.example.direa.repository.QueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    String quotation_section = "";
    String rest_section = "";
    String search_hits;

    private MetaRepository metaRepository;

    @Autowired
    private QueryRepository queryRepository;

    //  TEST 검색 서비스 로직
    // TODO: 2021/08/13  반환 값 설정
    public List<MetaDataIndex> searchAll() throws IOException {
        // repository 로 데이터 가져오기
        SearchResponse searchResponse = metaRepository.findAllMetaData();
        // 반환된 response hit 수
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();

        int numHits = (int) totalHits.value;
        log.info("total hit : {}",numHits);

        List<MetaDataIndex> metaDataIndexList = new ArrayList<>();

        hits.forEach(hit -> {
            Map<String,Object> result  = hit.getSourceAsMap();
            metaDataIndexList.add(MetaDataIndex.builder()
                    .title(String.valueOf(result.get("title")))
                    .host(String.valueOf(result.get("host")))
                    .timestamp(Date.valueOf((String)result.get("@timestamp")))
                    .path(String.valueOf(result.get("path")))
                    .text_file_name(String.valueOf(result.get("text_file_name")))
                    .label(String.valueOf(result.get("label")))
                    .dep(String.valueOf(result.get("dep")))
                    .build());
        });
        return metaDataIndexList;
    }



    //설계에 기반한 구현
    public List<Dsearch> get_search (String input , int page) throws IOException {
        //double_quotation 없을 때
       // if(!check_quotation(input)){


            //오탈자 확인
//            SearchResponse searchResponse = spell_check(input);
//            SearchHits hits = searchResponse.getHits();
//            TotalHits totalHits = hits.getTotalHits();
//
//            if((int) totalHits.value != 0 ){
//                //오탈자 변경 값이 있을 떄
//                String original_input = input;
//
//                Suggest suggest = searchResponse.getSuggest();
//                TermSuggestion termSuggestion = suggest.getSuggestion("my_suggester");
//                input =  termSuggestion.getEntries().get(0).getText().toString();
//            }


//            //동의어 체크
//            searchResponse = synonym(input);
//            hits =  searchResponse.getHits();
//            totalHits =  hits.getTotalHits();
//            String synonym_input = "";
//            if((int) totalHits.value != 0) {
//                //동의어 input에 추가
//               Map<String,Object> result =  hits.getAt(0).getSourceAsMap();
//               synonym_input = String.valueOf(result.get("synonym"));
//            }

            //meta data 인덱스에 날릴 차례
            SearchResponse searchResponse =  queryRepository.findTextDataByBool(input,page);
            SearchHits hits = searchResponse.getHits();

            search_hits = String.valueOf(hits.getTotalHits().value);
            log.info("totalhits",search_hits);

            List<Dsearch> dsearchList = new ArrayList<>();


            if(!hits.equals(null)) {
                hits.forEach(hit -> {
                    log.info("힛결과 포이치");
                    Map<String, Object> result = hit.getSourceAsMap();
                    HashMap<String, Object> core = (HashMap<String, Object>) result.get("core");

                    dsearchList.add(Dsearch.builder()
                            .title(String.valueOf(core.get("title")))
                            .doc_date(String.valueOf(core.get("doc_date")))
                            .path(String.valueOf(core.get("path")))
                            .label(String.valueOf(core.get("label")))
                            .dep(String.valueOf(result.get("dep")))
                            .html(String.valueOf(core.get("html")))
                            .contents(String.valueOf(core.get("contents")))
                            .text_file_path(String.valueOf(result.get("text_file_path")))
                            .build());

                    log.info("Response 결과", result);
                    System.out.println(result);
                    log.info("result.get('core.contents') 값 확안");
                    System.out.println(core.get("contents"));
                });
            }
            return dsearchList;

//        }else{
//            //double_quotation 존재
//
//
//        }
//
//        List<MetaDataIndex> metaDataIndexList =  new ArrayList<>();
//
//        return metaDataIndexList;
    }

    public String getTotalHits(){
        return search_hits;
    }

    //따옴표 체크 함수
    private Boolean check_quotation(String input)  {
        if(input.contains("\"")){
            return true;
        }else{
            return false;
        }
    }

    //따옴표 부분 분리
    private void inputTokenizing(String input) {
        quotation_section = input.split("\"")[0];
        rest_section =  input.split("\"")[1];
    }

    //오탈자 교정
    private SearchResponse spell_check(String input) throws IOException {
        SearchResponse searchResponse = queryRepository.findSpellCheckBySuggester(input);
        return searchResponse;
    }

    //동의어 확인
    private SearchResponse synonym(String input) throws IOException {
        SearchResponse searchResponse =  queryRepository.findSynonymByMatch(input);
        return searchResponse;
    }


    // TODO 검색한 결과에 대해 인기검색어 count 반영


}
