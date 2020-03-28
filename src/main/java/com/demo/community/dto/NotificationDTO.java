package com.demo.community.dto;

import com.demo.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {

    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long   notifierId;
    private String notifierName;
    private Long outerId;
    private String outerTitle;
    private String typeName;
    private Integer type;
}
