package com.quack.talk.api.view.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyTokFriendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
//    private String nickName;
    private String image;

}
