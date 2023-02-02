package com.quack.talk.api.report.dto;

import com.quack.talk.api.report.entity.ReportContents;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String targetId;
    private ReportContents.TargetType type;
    private String code;
    private String content;
    private String detail;
    private String status;
    private String createTime;

}
