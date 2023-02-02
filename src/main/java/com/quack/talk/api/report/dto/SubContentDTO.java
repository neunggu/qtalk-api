package com.quack.talk.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SubContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String content;

}
