package com.quack.talk.api.util;

import com.quack.talk.api.room.dto.server.Server;
import com.quack.talk.api.room.dto.server.ServerConfig;
import com.quack.talk.api.room.repository.ServerConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public  class ServerUtil {

    private final ServerConfigRepository serverConfigRepository;

    public Server getServerInfo(){
        ServerConfig serverConfig= serverConfigRepository.getServerConfig();
        Random r = new Random();
        List<Server> servers = serverConfig.getServers();
        int i = r.nextInt(servers.size());
        return servers.get(i);
    }

//    public String getServerNick(){
//        return serverConfigRepository.getServerNick();
//    }

}
