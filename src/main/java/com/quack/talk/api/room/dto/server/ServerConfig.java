package com.quack.talk.api.room.dto.server;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServerConfig implements Serializable {

    private String _id;
    private String env;
    private List<Server> servers;

}
