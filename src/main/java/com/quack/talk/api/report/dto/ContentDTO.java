package com.quack.talk.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subTitle;
    private List<SubContentDTO> subContent;

}
