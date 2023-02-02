package com.quack.talk.api.aspect.history;

import com.quack.talk.api.room.repository.RoomRepository;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.entity.SearchHistory;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Aspect
@Component
@RequiredArgsConstructor
public class History {

    private final RoomRepository roomRepository;

    @After("@annotation(com.quack.talk.api.aspect.history.AddSearchHistory)")
    public void addSearchHistory(JoinPoint joinPoint) {
        String keyword = (String) joinPoint.getArgs()[0];
        Friend myinfo = (Friend) joinPoint.getArgs()[1];

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
        String currentDateTime = sdf.format(System.currentTimeMillis());

        roomRepository.addSearchHistory(new SearchHistory(keyword, myinfo.getId(),currentDateTime));
    }
}
