package com.quack.talk.api.friend.mapper;

import com.quack.talk.common.friend.entity.Friend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper {

    int upsertFriendInfo(Friend friend);

    List<Friend> findAll(String myId);

    Friend findFriendWithRequestStatus(String myId, String friendId);

    Friend findFriendByEmail(String email);

    Friend findFriendStatus(String myId, String friendId);

    int insertFriendStatus(String myId, String friendId, int status);

    int insertFriendBoth(String myId, String friendId);

    int insertFriendStatusBoth(String myId, String friendId, int status);
}
