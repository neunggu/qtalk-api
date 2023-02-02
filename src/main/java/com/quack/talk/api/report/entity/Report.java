package com.quack.talk.api.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Report {

    private String userId;
    private String targetId;
    private String type;
    private String code;
    private String content;
    private String detail;
    private String status;
    private String createTime;


}
