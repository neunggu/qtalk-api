package com.quack.talk.api.friend.service;

import com.quack.talk.api.cache.FriendCache;
import com.quack.talk.api.cache.UserCache;
import com.quack.talk.api.friend.repository.FriendRepository;
import com.quack.talk.api.util.SearchType;
import com.quack.talk.api.util.mapper.QtalkMapper;
import com.quack.talk.common.friend.dto.FriendDTO;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService {

    private final UserCache userCache;
    private final FriendCache friendCache;
    private final QtalkMapper mapper;
    private final ChatUtil chatUtil;

    private final FriendRepository repository;

    public List<FriendDTO> findAll(Friend myInfo) {
        //　현재 상태값과 같이 가져오기
        List<Friend> allFriend = friendCache.findAll(myInfo.getId());
        Friend[] friends = chatUtil.toArray(allFriend, Friend.class);
        return mapper.map(friends);
    }

    @Transactional
    public FriendDTO search(Friend myInfo, String keyword, SearchType type) {
        Friend friend = userCache.search(keyword, type);
        if (ObjectUtils.isEmpty(friend)) return null;
        friendCache.upsertFriendInfo(friend);
        //　현재 상태값과 같이 가져오기
        Friend friendWithRequestStatus = repository.findFriendWithRequestStatus(myInfo.getId(), friend.getId());
        return mapper.map(friendWithRequestStatus);
    }

    @Transactional
    public FriendDTO request(Friend myInfo, String email) {
        Friend friend = friendCache.findFriendByEmail(email);
        Friend friendWithRequestStatus = repository.findFriendWithRequestStatus(myInfo.getId(), friend.getId());
        FriendDTO friendDTO = mapper.map(friendWithRequestStatus);
        if (friendDTO.getRequestStatus() == 0) {
            // 친구 요청
            repository.insertFriendStatus(myInfo.getId(), friend.getId(), 100);
            //요청 상태값 셋팅
            friendDTO.setRequestStatus(100);
            //TODO 친구 요청 알림
        }
        return friendDTO;
    }

    @Transactional
    public FriendDTO approve(Friend myInfo, String email) {
        Friend friend = friendCache.findFriendByEmail(email);
        Friend friendWithRequestStatus = repository.findFriendWithRequestStatus(myInfo.getId(), friend.getId());
        FriendDTO friendDTO = mapper.map(friendWithRequestStatus);
        if (friendDTO.getRequestStatus() == 100) {
            // 요청수락 status 추가
            // 요청 수락(101) 필요한가? 요청 수락 == 친구(200)
            friendDTO.setRequestStatus(101);
            repository.insertFriendStatus(myInfo.getId(), friend.getId(), 101);
            //친구 테이블 추가
            repository.insertFriendBoth(myInfo.getId(), friend.getId());
            repository.insertFriendStatusBoth(myInfo.getId(), friend.getId(), 200);
            //TODO 친구 승낙 알림
        }

        return friendDTO;
    }

    @Transactional
    public FriendDTO updateMyInfo(Friend myInfo) {
        friendCache.upsertFriendInfo(myInfo);
        return mapper.map(myInfo);
    }
}
