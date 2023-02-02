package com.quack.talk.api.room.dto;

import com.quack.talk.common.room.dto.RoomDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserRoomsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private List<RoomDTO> rooms;
}
