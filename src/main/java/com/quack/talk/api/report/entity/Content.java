package com.quack.talk.api.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Content {

    private String subTitle;
    private String code;
    private List<SubContent> subContent;

}
