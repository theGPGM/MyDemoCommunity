package com.demo.community.service;

import com.demo.community.dto.NotificationDTO;
import com.demo.community.dto.PaginationDTO;
import com.demo.community.enums.NotificationStatusEnum;
import com.demo.community.enums.NotificationTypeEnum;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import com.demo.community.mapper.NotificationMapper;
import com.demo.community.mapper.UserMapper;
import com.demo.community.model.Notification;
import com.demo.community.model.NotificationExample;
import com.demo.community.model.User;
import com.demo.community.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    //通知列表
    public PaginationDTO<NotificationDTO> list(Long userId, Integer page, Integer size) {

        //计算总通知数
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverIdEqualTo(userId);
        Integer count = (int) notificationMapper.countByExample(example);
        Integer totalPage = countTotalPage(count, page, size);
        if (totalPage == 0)
            return null;
        Integer offset = size * (page - 1);

        //单页通知
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<NotificationDTO>();

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverIdEqualTo(userId);
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));

        if(notifications.size() == 0) {
            return null;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.getTypeName(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setObjects(notificationDTOS);
        paginationDTO.setPagination(totalPage, page, size);
        return paginationDTO;
    }

    private Integer countTotalPage(Integer count, Integer page, Integer size) {
        Integer totalPage;
        if (count <= 0)
            return 0;
        //计算总页数
        if (count % size == 0) {
            totalPage = count / size;
        } else {
            totalPage = count / size + 1;
        }
        if (page < 1)
            page = 1;
        if (page > totalPage && totalPage != 0)
            page = totalPage;

        return totalPage;
    }

    public Long getUnreadCount(Long userId) {

        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverIdEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(example);
    }

    //更新为已读
    public NotificationDTO read(Long id, User user) {

        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification.getReceiverId() != user.getId())
            throw new CustomizeException(CustomizeErrorCode.READ_THE_NOTIFICATION_FAILED);
        if(notification == null)
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName( NotificationTypeEnum.getTypeName(notification.getType()));
        return notificationDTO;
    }
}
