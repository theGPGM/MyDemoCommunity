package com.demo.community.enums;

public enum  AuthUserTypeEnum {
    USERNAME_PASSWORD(1),
    GITHUB(2),
    WECHAT(3),
    QQ(4);

    private Integer type;

    public Integer getType() {
        return type;
    }

    AuthUserTypeEnum(Integer type) {
        this.type = type;
    }
}
