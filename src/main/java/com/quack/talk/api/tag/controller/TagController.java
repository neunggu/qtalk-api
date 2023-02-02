package com.quack.talk.api.tag.controller;

import com.quack.talk.api.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = {"/tag"})
public class TagController {

    private final TagService tagService;

    @GetMapping("/recommend/{type}")
    public ResponseEntity<?> recommend(@PathVariable int type){
        return ResponseEntity.ok(tagService.recommend(type));
    }

}
