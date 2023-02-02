package com.quack.talk.api.message.service;

import com.quack.talk.common.chat.dto.MessageDTO;
import com.quack.talk.common.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final ApiUtil apiUtil;
    @Value("${socketToMq.url}")
    private String socketToMqUrl;

    public void sendMessage(MessageDTO message) {
        // queue 호출
        String url = socketToMqUrl+"/chat/message";
        apiUtil.post(url, message, MessageDTO.class);
    }
}
