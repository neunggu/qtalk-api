package com.quack.talk.api.room.dto;

import com.quack.talk.common.friend.dto.FriendDTO;
import com.quack.talk.common.room.type.RoomType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateRoomDTO implements Serializable {

    private static final long serialVersionUID = 1L;

//    private MultipartFile image;
    private String title;
    private String introduce;
    private RoomType roomType;
    private List<String> tags;
    private boolean searchPermit;
    private List<FriendDTO> users;

}
