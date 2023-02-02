package com.quack.talk.api.cache;

import com.quack.talk.api.tag.repository.TagRepository;
import com.quack.talk.common.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TagCache {

    private final TagRepository repository;

    @Cacheable(value = "recommendTag", key = "#type", unless = "#result.size() == 0")
    public List<Tag> findRecommendTags(int type) {
        return repository.findRecommendTags(type);
    }

}
