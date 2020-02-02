package com.demo.comunity.service;

import com.demo.comunity.dto.PaginationDTO;
import com.demo.comunity.dto.QuestionDTO;
import com.demo.comunity.mapper.QuestionMapper;
import com.demo.comunity.mapper.UserMapper;
import com.demo.comunity.model.Question;
import com.demo.comunity.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;


    //返回首页文章列表对象
    public PaginationDTO select(Integer page, Integer size) {

        Integer totalPage;
        Integer count = questionMapper.count();
        //计算总页数
        if(count % size == 0){
            totalPage=count/size;
        }else{
            totalPage=count/size + 1;
        }
        if(page < 1)
            page = 1;
        if(page > totalPage)
            page = totalPage;

        Integer offset = size * (page - 1);
        List<Question> list = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();

        for (Question question : list) {
            User user = userMapper.findById(question.getCreatorId());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);
        paginationDTO.setPagination(totalPage,page, size);

        return paginationDTO;
    }
}
