package com.quack.talk.api.util;

public enum SearchType {
    EMAIL("email"),
    PHONE("phone"),
    QRCODE("qrcode");

    private String value;
    SearchType(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}
