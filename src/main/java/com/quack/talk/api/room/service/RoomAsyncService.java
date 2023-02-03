package com.quack.talk.api.room.service;

import com.quack.talk.api.room.repository.RoomRepository;
import com.quack.talk.common.fcm.dto.SubscriptionRequestDto;
import com.quack.talk.common.util.ApiUtil;
import com.quack.talk.common.util.FcmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableAsync
public class RoomAsyncService {

    private final RoomRepository chatRoomRepository;

    private final ApiUtil apiUtil;
    private final FcmUtil fcmUtil;

    @Value("${fcm.url}")
    private String fcmUrl;

    @Async
    public void deleteMessageBeforeLastMessageReadTimestamp(String roomId) {
        // 가장 늦은 소켓 아웃 시간 찾기
        long lastMessageReadTimestamp = chatRoomRepository.getLastMessageReadTimestamp(roomId);
        // 소켓 아웃 시간 이전 메세지 제거
        chatRoomRepository.deleteMessageBeforeLastMessageReadTimestamp(roomId, lastMessageReadTimestamp);
    }

    @Async
    public void fcmSubscribe(String roomId, String pushToken) {
        String topic = fcmUtil.getFcmTopic(roomId);
        SubscriptionRequestDto subscriptionRequestDto = SubscriptionRequestDto.builder()
                .topicName(topic)
                .tokens(Arrays.asList(pushToken))
                .build();
        String url = fcmUrl+"/noti/subscribe";
        apiUtil.post(url, subscriptionRequestDto, Map.class);
    }
}
