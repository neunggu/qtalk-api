package com.quack.talk.api.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReportContents {

    public enum TargetType {
        ROOM("room"), USER("user");

        String value;
        TargetType(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }

    private TargetType type;
    private List<Content> contents;

}
