package com.demo.community.service;

import com.demo.community.dto.LikeCommentDTO;
import com.demo.community.mapper.LikeCommentMapper;
import com.demo.community.model.LikeComment;
import com.demo.community.model.LikeCommentExample;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeCommentService {

    @Autowired
    LikeCommentMapper likeCommentMapper;

    public void create(LikeCommentDTO likeCommentDTO) {

        LikeComment likeComment = new LikeComment();
        BeanUtils.copyProperties(likeCommentDTO, likeComment);
        likeCommentMapper.insert(likeComment);
    }

    public boolean isLiked(LikeCommentDTO likeCommentDTO) {

        LikeCommentExample example = new LikeCommentExample();
        Long commentId = likeCommentDTO.getCommentId();
        Long likerId = likeCommentDTO.getLikerId();
        example.createCriteria()
                .andCommentIdEqualTo(commentId)
                .andLikerIdEqualTo(likerId);
        List<LikeComment> likeComments = likeCommentMapper.selectByExample(example);
        if(likeComments.size() != 0)
            return true;
        return false;
    }

    public void delete(LikeCommentDTO likeCommentDTO) {

        LikeCommentExample example = new LikeCommentExample();
        Long commentId = likeCommentDTO.getCommentId();
        Long likerId = likeCommentDTO.getLikerId();
        example.createCriteria()
                .andCommentIdEqualTo(commentId)
                .andLikerIdEqualTo(likerId);
        likeCommentMapper.deleteByExample(example);
    }
}
