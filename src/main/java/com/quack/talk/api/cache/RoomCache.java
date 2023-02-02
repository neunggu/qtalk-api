package com.quack.talk.api.cache;

import com.quack.talk.api.room.repository.RoomRepository;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.entity.Room;
import com.quack.talk.common.room.entity.RoomUsers;
import com.quack.talk.common.room.entity.UserRooms;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoomCache {

    private final RoomRepository repository;

    @Cacheable(value = "openTok", unless = "#result.size() == 0")
    public List<Room> findAllOpenTok() {
        return repository.findAllOpenTok();
    }

    @CacheEvict(value = "openTok", condition = "#room.roomType == 'OPEN_CHAT'")
    public void saveRoom(Room room) {
        repository.saveRoom(room);
    }

    @Cacheable(value = "roomDetail", key = "#roomId" , unless = "#result == null")
    public Room findRoom(String roomId, boolean searchPermit) {
        return repository.findRoom(roomId, searchPermit);
    }

//    @CacheEvict(value = "roomDetail", key = "#room.roomId")
//    public void updateRoomsUserInfo(Room room) {
//        repository.updateRoomsUserInfo(room);
//    }

    @CacheEvict(value = "roomDetail", key = "#room.roomId")
    public Room updateRoom(List<String> keys, Room room) {
        return repository.updateRoom(keys, room);
    }

    @Cacheable(value = "roomUsers", key = "#roomId", unless = "#result == null")
    public RoomUsers findRoomUsers(String roomId) {
        return repository.findRoomUsers(roomId);
    }

    @CacheEvict(value = "roomUsers", key = "#roomId")
    public void updateRoomUsers(String roomId, Friend user){
        repository.updateRoomUsers(roomId, user);
    }

    @Cacheable(value = "userRooms", key = "#userId", unless = "#result == null")
    public UserRooms findAllMyRooms(String userId) {
        return repository.findAllMyRooms(userId);
    }

    @CacheEvict(value = "userRooms", key = "#userId")
    public void updateUserRooms(String userId, String roomId){
        repository.updateUserRooms(userId, roomId);
    }

    @Cacheable(value = "searchRooms", key = "#keyword", unless = "#result.size() == 0")
    public List<Room> findRoomsByKeyword(String keyword) {
        return repository.findRoomsByKeyword(keyword);
    }


}
