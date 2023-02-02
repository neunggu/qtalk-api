package com.quack.talk.api.room.repository;

import com.quack.talk.api.room.dto.server.ServerConfig;
import com.quack.talk.common.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
// TODO:: mongodb, redis cache와 연결할것
public class ServerConfigRepository {

    private final MongoTemplate mongoTemplate;
    private final MongoUtil mongoUtil;

    @Value("${mongodb.collection.server_config}")
    private String COLLECTION_SERVER_CONFIG;

    @Value("${env}")
    private String env;

//    @Value("${server.nick}")
//    private String SERVER_NICK;

    public ServerConfig getServerConfig(){
        Query query = mongoUtil.getQuery("env", env);
        return mongoTemplate.findOne(query, ServerConfig.class, COLLECTION_SERVER_CONFIG);
    }

//    public String getServerNick(){
//        return SERVER_NICK.replace("\"", "");
//    }

}
