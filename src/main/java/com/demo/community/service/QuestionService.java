package com.demo.community.service;

import com.demo.community.dto.PaginationDTO;
import com.demo.community.dto.QuestionDTO;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import com.demo.community.mapper.QuestionExtendMapper;
import com.demo.community.mapper.QuestionMapper;
import com.demo.community.mapper.UserMapper;
import com.demo.community.model.Question;
import com.demo.community.model.QuestionExample;
import com.demo.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class    QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtendMapper questionExtendMapper;

    @Autowired
    private QuestionMapper questionMapper;


    //返回首页文章列表对象
    public PaginationDTO list(Integer page, Integer size) {

        Integer count = (int)questionMapper.countByExample(new QuestionExample());
        Integer totalPage = countTotalPage(count, page, size);
        if(totalPage == 0)
            return null;
        Integer offset = size * (page - 1);
        QuestionExample example = new QuestionExample();
        example.setOrderByClause("gmt_create desc");
        List<Question> list = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalPage,page, size);

        for (Question question : list) {
            User user = userMapper.selectByPrimaryKey(question.getCreatorId());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    //返回我的个人问题列表
    public PaginationDTO list(Long userId, Integer page, Integer size) {

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorIdEqualTo(userId);
        Integer count = (int)questionMapper.countByExample(example);
        Integer totalPage = countTotalPage(count, page, size);
        if(totalPage == 0)
            return null;
        Integer offset = size * (page - 1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorIdEqualTo(userId);
        List<Question> list = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalPage,page, size);

        for (Question question : list) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            User user = userMapper.selectByPrimaryKey(question.getCreatorId());
            questionDTO.setUser(user);

            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    //通过 id 返回
    public QuestionDTO getById(Long id) {

        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null)
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreatorId());
        questionDTO.setUser(user);

        return questionDTO;
    }

    private Integer countTotalPage(Integer count, Integer page, Integer size){
        Integer totalPage;
        if(count <= 0)
            return 0;
        //计算总页数
        if(count % size == 0){
            totalPage=count/size;
        }else{
            totalPage=count/size + 1;
        }
        if(page < 1)
            page = 1;
        if(page > totalPage && totalPage != 0)
            page = totalPage;

        return totalPage;
    }

    public void createOrUpdate(Question question) {

        if(question.getId() == null){
            //创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setViewCount(0);
            questionMapper.insert(question);
        }else{
            //更新问题
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setCommentCount(question.getCommentCount());
            updateQuestion.setLikeCount(question.getLikeCount());
            updateQuestion.setViewCount(question.getViewCount());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if(updated != 1)
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtendMapper.incView(question);
    }
}
