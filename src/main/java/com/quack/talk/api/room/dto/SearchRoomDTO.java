package com.quack.talk.api.room.dto;

import com.quack.talk.common.room.type.RoomType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchRoomDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roomId;
//    private String leaderId;
    private String photo;
    private RoomType roomType;
    private String title;
    private String introduce;
    private List<String> tags;
//    private List<FriendDTO> users;
    private int userCount;
//    private int qCount;

//    private String serverNick;

}
