package com.quack.talk.api.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TagRecommendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tag;

}
