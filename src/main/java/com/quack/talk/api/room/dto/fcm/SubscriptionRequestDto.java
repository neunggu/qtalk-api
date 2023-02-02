package com.quack.talk.api.room.dto.fcm;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubscriptionRequestDto {
    String topicName;
    List<String> tokens;
}
