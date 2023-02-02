package com.quack.talk.api.room.service;

import com.quack.talk.api.aspect.history.AddSearchHistory;
import com.quack.talk.api.cache.FriendCache;
import com.quack.talk.api.cache.RoomCache;
import com.quack.talk.api.room.dto.*;
import com.quack.talk.api.room.repository.RoomRepository;
import com.quack.talk.api.util.S3Util;
import com.quack.talk.api.util.ServerUtil;
import com.quack.talk.api.util.mapper.QtalkMapper;
import com.quack.talk.common.chat.entity.Message;
import com.quack.talk.common.friend.dto.FriendDTO;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.dto.RoomDTO;
import com.quack.talk.common.room.entity.Room;
import com.quack.talk.common.room.entity.RoomMesages;
import com.quack.talk.common.room.entity.RoomUsers;
import com.quack.talk.common.room.entity.UserRooms;
import com.quack.talk.common.room.type.RoomType;
import com.quack.talk.common.util.ApiUtil;
import com.quack.talk.common.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomCache roomCache;
    private final FriendCache friendCache;

    private final ChatUtil chatUtil;
    private final ServerUtil serverUtil;
    private final ApiUtil apiUtil;
    private final S3Util s3Util;

    private final QtalkMapper mapper;
    private final RoomRepository roomRepository;

    @Value("${socketToMq.url}")
    private String socketToMqUrl;

    public List<RoomDTO> findAllOpenTok() {
        List<Room> allOpenTok = roomCache.findAllOpenTok();
        Room[] rooms = chatUtil.toArray(allOpenTok, Room.class);
        return mapper.map(rooms);
    }

    public List<RoomDTO> findAllMyRooms(Friend myInfo) {
        UserRooms userRooms = roomCache.findAllMyRooms(myInfo.getId());
        UserRoomsDTO userRoomsDTO = mapper.map(userRooms);
        return userRoomsDTO.getRooms();
    }

    public RoomDTO findRoom(String roomId, boolean searchPermit) {
        Room room = roomCache.findRoom(roomId, searchPermit);
        return mapper.map(room);
    }

    @Transactional
    public RoomDTO createRoom(CreateRoomDTO createRoomDTO, MultipartFile file, Friend myInfo) {
        List<Friend> users = new ArrayList<>(Arrays.asList(myInfo));
        if (!CollectionUtils.isEmpty(createRoomDTO.getUsers())) {
            List<FriendDTO> friends = createRoomDTO.getUsers();
            for (FriendDTO f: friends) {
                if (!myInfo.getEmail().equals(f.getEmail())) {
                    Friend friendByEmail = friendCache.findFriendByEmail(f.getEmail());
                    users.add(friendByEmail);
                }
            }
        }
        // mongo save
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
        String currentDateTime = sdf.format(currentTimeMillis);
        // 데이터 준비
        RoomType roomType = createRoomDTO.getRoomType();
        String roomId = chatUtil.createRoomId(roomType, currentTimeMillis);
        String leaderId = myInfo.getId();
        // s3 업로드, return imageUrl
        String photo = s3Util.upload(file, roomType.getValue());
        String title = createRoomDTO.getTitle();
        String introduce = createRoomDTO.getIntroduce();
        List tags = createRoomDTO.getTags();
        boolean searchPermit = createRoomDTO.isSearchPermit();
        String serverNick = serverUtil.getServerInfo().getNick();

        Room room = Room.create(roomId, leaderId, photo, roomType, title,
                introduce, tags, searchPermit, users, serverNick, currentDateTime);
        roomCache.saveRoom(room);
        return mapper.map(room);
    }

    public RoomDTO updateRoom(UpdateRoomDTO updateRoomDto, MultipartFile file) {
        List<UpdateRoomDTO.UpdateKey> updateKeys = updateRoomDto.getUpdateKeys();
        if (updateKeys.contains(UpdateRoomDTO.UpdateKey.PHOTO)
                && !ObjectUtils.isEmpty(file)){
            RoomDTO roomDto = findRoom(updateRoomDto.getUpdateData().getRoomId(), false);
            String prevPhotoUrl = roomDto.getPhoto();
            s3Util.delete(prevPhotoUrl);
            String photo = s3Util.upload(file, updateRoomDto.getUpdateData().getRoomType().getValue());
            updateRoomDto.getUpdateData().setPhoto(photo);
        }

        // enum to string value
        List<String> updateKeysStr = updateKeys.stream()
                .map(key->key.getValue())
                .collect(Collectors.toList());
        Room room = mapper.map(updateRoomDto.getUpdateData());
        Room modifiedRoom = roomCache.updateRoom(updateKeysStr, room);
        return mapper.map(modifiedRoom);
    }

    @Transactional
    public RoomEnterDTO enterRoom(String roomId, Friend myInfo) {
        RoomDTO roomDto = findRoom(roomId, false);
        //user 추가
        FriendDTO myinfoDto = mapper.map(myInfo);
        // 방나간 시간 가져와서 셋팅, 처음 들어왔으면 0
        long lastMessageReadTimestamp = roomRepository.getLastMessageReadTimestamp(roomId, myInfo.getId());
        myinfoDto.setLastMessageReadTimestamp(lastMessageReadTimestamp);
        boolean isNew = lastMessageReadTimestamp == 0;

        myInfo = mapper.map(myinfoDto);
        roomCache.updateRoomUsers(roomId, myInfo);
        roomCache.updateUserRooms(myInfo.getId(), roomId);
        updateRoomsUserFour(roomDto);

        // 안읽은 메세지 가져오기
        List unReadMessages = getUnReadMessages(roomId, myInfo, isNew);
        //리스너 추가
        setChatListner(roomDto);
        return new RoomEnterDTO(roomDto, isNew, unReadMessages);
    }

    public void setChatListner(RoomDTO roomDto) {
        String url = socketToMqUrl+"/chat/setChatListener";
        apiUtil.post(url, roomDto, Room.class);
    }

    private void updateRoomsUserFour(RoomDTO roomDto){
        // room에 유저 리스트 최대 4개만 저장
        RoomUsers roomUsers = roomCache.findRoomUsers(roomDto.getRoomId());
        Friend[] friends = chatUtil.toArray(roomUsers.getUsers(), Friend.class);
        List<FriendDTO> usersDto = mapper.map(friends);
        int usersSize = usersDto.size();
        roomDto.setUserCount(usersSize);
        if (usersSize > 4){
            usersDto = usersDto.subList(0, 4);
        }
        roomDto.setUsers(usersDto);

        // update
        List<UpdateRoomDTO.UpdateKey> updateKeys
                = Arrays.asList(UpdateRoomDTO.UpdateKey.USER, UpdateRoomDTO.UpdateKey.USER_COUNT);
        updateRoom(new UpdateRoomDTO(updateKeys, roomDto), null);
    }

    private List<Message> getUnReadMessages(String roomId, Friend user, boolean isNew){
        List<Message> unReadMessages = null;
        if (!isNew){
            unReadMessages = roomRepository.getUnReadMessages(roomId, user);
            //오래된 순 정렬
            Collections.reverse(unReadMessages);
        }
        return unReadMessages;
    }

    public RoomMesages readMessage(String[] chatIds, Friend myInfo) {
        //메세지가 같이 올때 방아이디는 같다.
        String roomId = chatUtil.getRoomIdFromChatId(chatIds[0]);
        roomRepository.updateChatRoomUserLastMessageReadTimestamp(roomId, myInfo.getId());
        return roomRepository.minusUnReadCount(roomId, chatIds);
    }

    @AddSearchHistory
    public List<SearchRoomDTO> search(String keyword, Friend myInfo) {
        List<Room> searchResult = roomCache.findRoomsByKeyword(keyword);
        Room[] rooms = chatUtil.toArray(searchResult, Room.class);
        return mapper.mapToSearchDTO(rooms);
    }

    public List<FriendDTO> findRoomUsers(String roomId) {
        RoomUsers roomUsers = roomCache.findRoomUsers(roomId);
        List<Friend> usersResult = roomUsers.getUsers();
        Friend[] users = chatUtil.toArray(usersResult, Friend.class);
        return mapper.map(users);
    }
}
