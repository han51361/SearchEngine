package com.example.direa.controller;

import com.example.direa.data.Dsearch;
import com.example.direa.data.MetaDataIndex;
import com.example.direa.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
public class MainController {


    private final SearchService searchService;

    @Autowired
    public MainController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/main")
    public String main(Model model) throws IOException {
        return "realmain";
    }

    //인기 검색어 리스트 조회


    //검색
    @GetMapping("/search")
    public String search(String input, @RequestParam(value = "page", defaultValue = "1")Integer page, Model model) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(page < 1){
            page = 1 ;
        }
        List<Dsearch> dsearchList =  searchService.get_search(input,page);
        //log.info("콘텐츠 확인",dsearchList.get(0).getContents());
       // System.out.println(dsearchList.get(0).getContents());
        stopWatch.stop();
        String searchTime = String.valueOf(stopWatch.getLastTaskTimeMillis());
        String totalHits = searchService.getTotalHits();

        model.addAttribute("result_List",dsearchList);
        model.addAttribute("input_string",input);
        model.addAttribute("page_num",page);
        model.addAttribute("searchtime",searchTime);
        model.addAttribute("total_hits",totalHits);
        return "/searchResult";
    }



}