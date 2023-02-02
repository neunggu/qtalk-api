package com.quack.talk.api.view.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class MyTokDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<MyTokFriendDTO> myFriends;
    private List<MyTokRoomDTO> myToks;

}
