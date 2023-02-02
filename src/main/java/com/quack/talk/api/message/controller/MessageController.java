package com.quack.talk.api.message.controller;

import com.quack.talk.api.message.service.MessageService;
import com.quack.talk.common.chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = {"/chat"})
public class MessageController {

    private final MessageService service;

    @PostMapping("/message")
    public ResponseEntity sendChatMessage(@RequestBody MessageDTO message){
        service.sendMessage(message);
        return ResponseEntity.ok(message);
    }

}
