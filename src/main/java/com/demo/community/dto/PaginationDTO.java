package com.demo.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {

    List<QuestionDTO> questions;
    boolean showPrevious;
    boolean showFirst;
    boolean showNext;
    boolean showEnd;
    Integer currPage;
    Integer totalPage;
    Integer previousPage;
    Integer nextPage;
    List<Integer> pages = new ArrayList<>();

    public void setPagination( Integer totalPage,Integer page, Integer size) {

        this.totalPage = totalPage;
        this.currPage = page;
        if(page < 1) {
            page = 1;
        }
        if(page > totalPage) {
            page = totalPage;
        }


        pages.add(page);
        for(int i = 1; i <= 3; i++){
            if(page - i > 0)
                pages.add(0, page-i);

            if(page + i <= totalPage)
                pages.add(page + i);
        }

        //是否显示上一页
        if(page==1){
            showPrevious = false;
        }
        else{
            showPrevious = true;
            previousPage = page - 1;
        }
        //是否显示下一页
        if(page == totalPage){
            showNext = false;
        }else{
            showNext = true;
            nextPage = page + 1;
        }
        //是否展示第一页
        if(pages.contains(1)){
            showFirst = false;
        }else{
            showFirst = true;
        }
        //是否展示最后一页
        if(pages.contains(totalPage)){
            showEnd = false;
        }
        else{
            showEnd = true;
        }
    }
}
