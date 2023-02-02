package com.quack.talk.api.room.dto.server;

import lombok.Data;

import java.io.Serializable;

@Data
public class Server implements Serializable {

    private String ip;
    private String port;
    private String nick;

}
