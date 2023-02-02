package com.quack.talk.api.room.dto;

import com.quack.talk.common.chat.entity.Message;
import com.quack.talk.common.room.dto.RoomDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoomEnterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private RoomDTO room;
    private boolean isNewEnterUser;
    private List<Message> unReadMessages;

    public RoomEnterDTO(RoomDTO room, boolean isNewEnterUser, List unReadMessages) {
        this.room = room;
        this.isNewEnterUser = isNewEnterUser;
        this.unReadMessages = unReadMessages;
    }
}
