package com.quack.talk.api.view.dto;

import com.quack.talk.common.room.type.RoomType;
import lombok.Data;

import java.io.Serializable;

@Data
public class MyTokRoomDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roomId;
    private String photo;
    private RoomType roomType;
    private String title;

}
