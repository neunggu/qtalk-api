package com.quack.talk.api.friend.repository;

import com.quack.talk.api.friend.mapper.FriendMapper;
import com.quack.talk.common.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FriendRepository {

    private final FriendMapper mapper;

    public int upsertFriendInfo(Friend friend) {
        return mapper.upsertFriendInfo(friend);
    }

    public List<Friend> findAll(String myId){
        return mapper.findAll(myId);
    }

    public Friend findFriendWithRequestStatus(String myId, String friendId){
        return mapper.findFriendWithRequestStatus(myId, friendId);
    }

    public Friend findFriendByEmail(String email) {
        return mapper.findFriendByEmail(email);
    }

    public Friend findFriendStatus(String myId, String friendId) {
        return mapper.findFriendStatus(myId, friendId);
    }

    public int insertFriendStatus(String myId, String friendId, int status) {
        return mapper.insertFriendStatus(myId, friendId, status);
    }

    public int insertFriendBoth(String myId, String friendId) {
        return mapper.insertFriendBoth(myId, friendId);
    }

    public int insertFriendStatusBoth(String myId, String friendId, int status) {
        return mapper.insertFriendStatusBoth(myId, friendId, status);
    }
}
