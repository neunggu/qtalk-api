package com.quack.talk.api.cache;

import com.quack.talk.api.friend.repository.FriendRepository;
import com.quack.talk.common.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendCache {

    private final FriendRepository repository;

    @Cacheable(value = "friendAll", key = "#id", unless = "#result.size() == 0")
    public List<Friend> findAll(String id) {
        return repository.findAll(id);
    }

    @CacheEvict(value = "friend", key = "#friend.id")
    public void upsertFriendInfo(Friend friend) {
        repository.upsertFriendInfo(friend);
    }

    @Cacheable(value = "friend", key = "#email", unless = "#result == null")
    public Friend findFriendByEmail(String email) {
        return repository.findFriendByEmail(email);
    }

}
