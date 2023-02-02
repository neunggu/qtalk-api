package com.quack.talk.api.room.dto;

import com.quack.talk.common.room.dto.RoomDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum UpdateKey {
        LEADER("leaderId"),
        PHOTO("photo"),
        TITLE("title"),
        INTRODUCE("introduce"),
        TAG("tags"),
        USER("users"),
        USER_COUNT("userCount");

        String value;
        UpdateKey(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }

    private List<UpdateKey> updateKeys;
    private RoomDTO updateData;
}
