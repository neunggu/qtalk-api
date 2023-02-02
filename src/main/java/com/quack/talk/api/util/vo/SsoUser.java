package com.quack.talk.api.util.vo;

import lombok.Data;

@Data
public class SsoUser {
    private String id;
    private String origin_name;
    private String name;
    private String email;
    private String phone;
    private String qrCode;
    private String imgKey;
    private String status;
    private String statusMsg;
    private String push_token;
    private String loginType;
    private String userType;
}
