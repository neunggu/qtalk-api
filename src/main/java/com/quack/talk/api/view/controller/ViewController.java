package com.quack.talk.api.view.controller;

import com.quack.talk.api.cache.UserCache;
import com.quack.talk.api.view.service.ViewService;
import com.quack.talk.common.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = {"/view"})
public class ViewController {

    private final UserCache userCache;
    private final ViewService viewService;

    @GetMapping("/openTok")
    public ResponseEntity<?> openTok(){
        return ResponseEntity.ok(viewService.openTok());
    }

    @GetMapping("/myTok")
    public ResponseEntity<?> myTok(@RequestHeader("Authorization") String accessToken){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(viewService.myTok(myInfo));
    }

    @GetMapping("/myTok/tagArea")
    public ResponseEntity<?> tagArea(@RequestHeader("Authorization") String accessToken){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(viewService.tagArea(myInfo));
    }

    @GetMapping("/myTok/tagArea/{keyword}")
    public ResponseEntity<?> searchRoomByTag(@RequestHeader("Authorization") String accessToken,
                                                @PathVariable String keyword){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(viewService.searchRoomByTag(keyword, myInfo));
    }

}
