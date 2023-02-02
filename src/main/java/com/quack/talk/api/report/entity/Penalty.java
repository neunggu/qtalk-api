package com.quack.talk.api.report.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Penalty {

    private int code;
    private String targetId;
    private ReportContents.TargetType type;
    private String startDate;
    private String endDate;
    private String status;

}
