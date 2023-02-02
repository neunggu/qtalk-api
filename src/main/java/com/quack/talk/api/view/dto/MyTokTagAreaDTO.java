package com.quack.talk.api.view.dto;

import com.quack.talk.api.room.dto.SearchRoomDTO;
import com.quack.talk.api.tag.dto.TagRecommendDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class MyTokTagAreaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TagRecommendDTO> tags;
    private List<SearchRoomDTO> rooms;

}
