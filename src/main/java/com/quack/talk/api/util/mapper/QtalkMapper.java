package com.quack.talk.api.util.mapper;

import com.quack.talk.api.report.dto.ContentDTO;
import com.quack.talk.api.report.dto.ReportDTO;
import com.quack.talk.api.report.entity.Content;
import com.quack.talk.api.report.entity.Report;
import com.quack.talk.api.room.dto.SearchRoomDTO;
import com.quack.talk.api.room.dto.UserRoomsDTO;
import com.quack.talk.api.tag.dto.TagRecommendDTO;
import com.quack.talk.api.view.dto.MyTokFriendDTO;
import com.quack.talk.api.view.dto.MyTokRoomDTO;
import com.quack.talk.common.friend.dto.FriendDTO;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.dto.RoomDTO;
import com.quack.talk.common.room.entity.Room;
import com.quack.talk.common.room.entity.UserRooms;
import com.quack.talk.common.tag.entity.Tag;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QtalkMapper {
    RoomDTO map(Room obj);

    @InheritInverseConfiguration
    Room map(RoomDTO obj);

    FriendDTO map(Friend obj);

    @InheritInverseConfiguration
    Friend map(FriendDTO obj);

    UserRoomsDTO map(UserRooms obj);

    @InheritInverseConfiguration
    UserRooms map(UserRoomsDTO obj);

    Report map(ReportDTO obj);

    List<RoomDTO> map(Room[] obj);

    List<FriendDTO> map(Friend[] obj);

    List<SearchRoomDTO> mapToSearchDTO(Room[] obj);

    List<MyTokFriendDTO> mapToMyTokDTO(FriendDTO[] obj);

    List<MyTokRoomDTO> mapToMyTokDTO(RoomDTO[] obj);

    List<TagRecommendDTO> mapToTagDTO(Tag[] obj);

    List<ContentDTO> mapToReportContentDTO(Content[] obj);


}
