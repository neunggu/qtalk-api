package com.quack.talk.api.tag.mapper;

import com.quack.talk.common.tag.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {

    List<Tag> findRecommendTags(int type);
}
