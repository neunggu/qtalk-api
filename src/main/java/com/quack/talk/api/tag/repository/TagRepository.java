package com.quack.talk.api.tag.repository;

import com.quack.talk.api.tag.mapper.TagMapper;
import com.quack.talk.common.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TagRepository {
    private final TagMapper mapper;

    public List<Tag> findRecommendTags(int type) {
        return mapper.findRecommendTags(type);
    }
}
