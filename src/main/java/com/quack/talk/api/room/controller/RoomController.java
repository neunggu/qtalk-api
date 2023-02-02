package com.quack.talk.api.room.controller;

import com.quack.talk.api.cache.UserCache;
import com.quack.talk.api.room.dto.CreateRoomDTO;
import com.quack.talk.api.room.dto.RoomEnterDTO;
import com.quack.talk.api.room.dto.UpdateRoomDTO;
import com.quack.talk.api.room.service.RoomAsyncService;
import com.quack.talk.api.room.service.RoomService;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.entity.RoomMesages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = {"/room"})
public class RoomController {

    private final UserCache userCache;
    private final RoomService roomService;
    private final RoomAsyncService roomAsyncService;

    @GetMapping("/all")
    public ResponseEntity<?> findAllRooms(){
        return ResponseEntity.ok(roomService.findAllOpenTok());
    }

    @GetMapping("/my")
    public ResponseEntity<?> findAllMyRooms(@RequestHeader("Authorization") String accessToken){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(roomService.findAllMyRooms(myInfo));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> findRoom(@PathVariable String roomId){
        return ResponseEntity.ok(roomService.findRoom(roomId, true));
    }

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String accessToken,
                                        @RequestPart MultipartFile file,
                                        @RequestPart CreateRoomDTO data){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(roomService.createRoom(data, file, myInfo));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRoom(@RequestHeader("Authorization") String accessToken,
                                        @RequestPart(required = false) MultipartFile file,
                                        @RequestPart UpdateRoomDTO data){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(roomService.updateRoom(data, file));
    }

    @GetMapping("/enter")
    public ResponseEntity<?> enterRoom(@RequestHeader("Authorization") String accessToken,
                                       @RequestParam String roomId){
        Friend myInfo = userCache.getMyinfo(accessToken);
        RoomEnterDTO roomEnterDto = roomService.enterRoom(roomId, myInfo);
        //fcm 구독
        roomAsyncService.fcmSubscribe(roomId, myInfo.getPushToken());
        // 사람들이 다 읽은 메세지는 삭제
        roomAsyncService.deleteMessageBeforeLastMessageReadTimestamp(roomId);
        return ResponseEntity.ok(roomEnterDto);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> search(@RequestHeader("Authorization") String accessToken,
                                    @PathVariable String keyword){
        Friend myInfo = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(roomService.search(keyword, myInfo));
    }

    @GetMapping("/users")
    public ResponseEntity<?> findRoomUsers(@RequestHeader("Authorization") String accessToken,
                                            @RequestParam String roomId){
        Friend user = userCache.getMyinfo(accessToken);
        return ResponseEntity.ok(roomService.findRoomUsers(roomId));
    }

    @PostMapping("/read")
    public ResponseEntity<?> readMessage(@RequestHeader("Authorization") String accessToken,
                                         @RequestBody String[] chatIds){
        Friend user = userCache.getMyinfo(accessToken);
        RoomMesages roomMessages = roomService.readMessage(chatIds, user);
        return ResponseEntity.ok(roomMessages);
    }

}
