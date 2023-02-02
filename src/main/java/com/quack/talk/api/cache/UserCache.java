package com.quack.talk.api.cache;

import com.quack.talk.api.util.SearchType;
import com.quack.talk.api.util.SsoUtil;
import com.quack.talk.common.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCache {

    private final SsoUtil ssoUtil;

    @Cacheable(value = "user", key = "#accessToken", unless = "#result == null")
    public Friend getMyinfo(String accessToken) {
        return ssoUtil.getMyinfo(accessToken);
    }

    @CachePut(value = "user", key = "#accessToken")
    public Friend getAndUpdateCacheMyinfo(String accessToken) {
        return ssoUtil.getMyinfo(accessToken);
    }

    @Cacheable(value = "user", key = "#keyword", unless = "#result == null")
    public Friend search(String keyword, SearchType type) {
        return ssoUtil.search(keyword, type);
    }
}
