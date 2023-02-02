package com.quack.talk.api.friend.controller;

import com.quack.talk.api.cache.UserCache;
import com.quack.talk.api.friend.service.FriendService;
import com.quack.talk.api.util.SearchType;
import com.quack.talk.common.friend.dto.FriendDTO;
import com.quack.talk.common.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = {"/friend"})
public class FriendController {

    private final UserCache userCache;
    private final FriendService friendService;

    @PostMapping("/updateMyInfo")
    public ResponseEntity<?> updateMyInfo(@RequestHeader("Authorization") String accessToken){
        Friend myInfo = userCache.getAndUpdateCacheMyinfo(accessToken);
        return ResponseEntity.ok(friendService.updateMyInfo(myInfo));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestHeader("Authorization") String accessToken){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(friendService.findAll(myInfo));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> search(@RequestHeader("Authorization") String accessToken,
                                    @PathVariable String keyword,
                                    @RequestParam SearchType type){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(friendService.search(myInfo, keyword, type));
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestHeader("Authorization") String accessToken,
                                     @RequestBody String email){
        Friend myInfo = userCache.getMyinfo(accessToken);
        try {
            FriendDTO friend = friendService.request(myInfo, email);
            if (ObjectUtils.isEmpty(friend)) {
                throw new Exception();
            }
            return ResponseEntity.ok(friend);
        } catch (Exception e ) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approve(@RequestHeader("Authorization") String accessToken,
                                     @RequestBody String email){
        Friend myInfo = userCache.getMyinfo(accessToken);
        try {
            FriendDTO friend = friendService.approve(myInfo, email);
            if (ObjectUtils.isEmpty(friend)) {
                throw new Exception();
            }
            return ResponseEntity.ok(friend);
        } catch (Exception e ) {
            return ResponseEntity.badRequest().build();
        }
    }


}
