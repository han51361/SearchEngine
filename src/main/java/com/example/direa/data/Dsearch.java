package com.example.direa.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Builder
public class Dsearch {
    //검색 결과 수
    public String hits;
    //인덱스 시간
    public String doc_date;
    //문서 이름
    public String title;
    //부서이름
    public String dep;

    public String contents;
    //문서 카테고리
    public String label;
    //문서 서버 경로
    public String path;
    //파일 이름

    public String text_file_path;

    public String html;
}
