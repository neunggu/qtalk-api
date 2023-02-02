package com.quack.talk.api.view.service;

import com.quack.talk.api.friend.service.FriendService;
import com.quack.talk.api.room.dto.SearchRoomDTO;
import com.quack.talk.api.room.service.RoomService;
import com.quack.talk.api.tag.dto.TagRecommendDTO;
import com.quack.talk.api.tag.service.TagService;
import com.quack.talk.api.util.mapper.QtalkMapper;
import com.quack.talk.api.view.dto.MyTokDataDTO;
import com.quack.talk.api.view.dto.MyTokFriendDTO;
import com.quack.talk.api.view.dto.MyTokRoomDTO;
import com.quack.talk.api.view.dto.MyTokTagAreaDTO;
import com.quack.talk.common.friend.dto.FriendDTO;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.dto.RoomDTO;
import com.quack.talk.common.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ViewService {

    private final RoomService roomService;
    private final FriendService friendService;
    private final TagService tagService;
    private final QtalkMapper mapper;
    private final ChatUtil chatUtil;

    public Object openTok() {
        return null;
    }

    public MyTokDataDTO myTok(Friend myInfo) {
        // friends
        List<FriendDTO> friendDto = friendService.findAll(myInfo);
        FriendDTO[] friendDtoArr = chatUtil.toArray(friendDto, FriendDTO.class);
        List<MyTokFriendDTO> myTokFriendDto = mapper.mapToMyTokDTO(friendDtoArr);
        // rooms
        List<RoomDTO> roomDto = roomService.findAllMyRooms(myInfo);
        RoomDTO[] roomDtoArr = chatUtil.toArray(roomDto, RoomDTO.class);
        List<MyTokRoomDTO> myTokRoomDto = mapper.mapToMyTokDTO(roomDtoArr);
        return new MyTokDataDTO(myTokFriendDto, myTokRoomDto);
    }

    public MyTokTagAreaDTO tagArea(Friend myInfo) {
        List<TagRecommendDTO> tags = tagService.recommend(1);
        if (tags.size() == 0 ) return null;

        List<SearchRoomDTO> search = searchRoomByTag(tags.get(0).getTag(), myInfo);
        return new MyTokTagAreaDTO(tags, search);
    }

    public List<SearchRoomDTO> searchRoomByTag(String keyword, Friend myInfo) {
        List<SearchRoomDTO> search = roomService.search(keyword, myInfo);
        // 랜덤 최대 4개만 리턴
        Collections.shuffle(search);
        int limit = search.size() >= 4 ? 4 : search.size();
        return search.subList(0,limit);
    }
}
