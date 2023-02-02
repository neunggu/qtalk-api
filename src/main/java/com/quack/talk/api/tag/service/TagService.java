package com.quack.talk.api.tag.service;

import com.quack.talk.api.cache.TagCache;
import com.quack.talk.api.tag.dto.TagRecommendDTO;
import com.quack.talk.api.util.mapper.QtalkMapper;
import com.quack.talk.common.tag.entity.Tag;
import com.quack.talk.common.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagCache tagCache;
    private final QtalkMapper mapper;
    private final ChatUtil chatUtil;

    public List<TagRecommendDTO> recommend(int type) {
        List<Tag> recommendTags = tagCache.findRecommendTags(type);
        // 랜덤 8개 추출
        Collections.shuffle(recommendTags);
        int limit = recommendTags.size() >= 8 ? 8 : recommendTags.size();
        Tag[] recommendTagsArr = chatUtil.toArray(recommendTags.subList(0,limit), Tag.class);
        return mapper.mapToTagDTO(recommendTagsArr);
    }
}
