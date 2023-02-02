package com.quack.talk.api.room.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import com.quack.talk.common.chat.entity.Message;
import com.quack.talk.common.friend.entity.Friend;
import com.quack.talk.common.room.entity.*;
import com.quack.talk.common.room.type.RoomType;
import com.quack.talk.common.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RoomRepository {
    private final MongoTemplate mongoTemplate;
    private final MongoUtil mongoUtil;

    @Value("${mongodb.collection.rooms}")
    private String COLLECTION_ROOMS;
    @Value("${mongodb.collection.users_rooms}")
    private String COLLECTION_USERS_ROOMS;
    @Value("${mongodb.collection.rooms_users}")
    private String COLLECTION_ROOMS_USERS;
    @Value("${mongodb.collection.rooms_messages}")
    private String COLLECTION_ROOMS_MESSAGES;
    @Value("${mongodb.collection.history_search}")
    private String COLLECTION_HISTORY_SEARCH;

    public void saveRoom(Room room) {
        mongoTemplate.save(room, COLLECTION_ROOMS);
    }

    public List<Room> findAllOpenTok(){
        Query query = mongoUtil.getQuery("roomType", RoomType.OPEN_CHAT.toString())
                        .addCriteria(Criteria.where("searchPermit").is(true));
        List<Room> rooms = mongoTemplate.find(query, Room.class, COLLECTION_ROOMS);
        return rooms;
    }

    public UserRooms findAllMyRooms(String userId) {
        AggregationOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        LookupOperation lookup = LookupOperation.newLookup()
                .from(COLLECTION_ROOMS)
                .localField("roomIds")
                .foreignField("roomId")
                .as("rooms");
        Aggregation aggregation = Aggregation.newAggregation(match, lookup);
        UserRooms UserRooms = mongoTemplate.aggregate(aggregation, COLLECTION_USERS_ROOMS, UserRooms.class)
                                    .getUniqueMappedResult();

        return UserRooms;
    }

    public Room findRoom(String roomId, boolean searchPermit) {
        Query query = mongoUtil.getQuery("roomId", roomId);
        if (searchPermit) {
            query.addCriteria(Criteria.where("searchPermit").is(searchPermit));
        }
        Room room = mongoTemplate.findOne(query, Room.class, COLLECTION_ROOMS);
        return room;
    }

    public void updateUserRooms(String userId, String roomId) {
        Query query = mongoUtil.getQuery("userId", userId);
        Update update = mongoUtil.getUpdate().addToSet("roomIds", roomId);
        mongoTemplate.upsert(query, update, COLLECTION_USERS_ROOMS);
    }

    public void updateRoomUsers(String roomId, Friend user) {
        Query query = mongoUtil.getQuery("roomId", roomId);

        // 기존 데이터 제거
        String pullQuery = "{'_id':'"+user.getId()+"'}";
        Update updatePull = mongoUtil.updatePull("users", BasicDBObject.parse(pullQuery));
        mongoTemplate.updateMulti(query, updatePull, COLLECTION_ROOMS_USERS);

        // 새로운 데이터 추가
        Update updatePush = mongoUtil.updatePush("users", user);
        mongoTemplate.upsert(query, updatePush, COLLECTION_ROOMS_USERS);
    }

    public long getLastMessageReadTimestamp(String roomId, String userId){
        Query query = mongoUtil.getQuery("roomId", roomId);
        query.addCriteria(Criteria.where("users._id").is(userId));
        RoomUsers roomUsers = mongoTemplate.findOne(query, RoomUsers.class, COLLECTION_ROOMS_USERS);
        long lastMessageReadTimestamp = 0;
        if (!ObjectUtils.isEmpty(roomUsers)) {
            List<Friend> users = roomUsers.getUsers();
            for (Friend f:users) {
                if (f.getId().equals(userId)){
                    lastMessageReadTimestamp = f.getLastMessageReadTimestamp();
                    break;
                }
            }
        }
        return lastMessageReadTimestamp;
    }

    public RoomUsers findRoomUsers(String roomId) {
        Query query = mongoUtil.getQuery("roomId", roomId);
        RoomUsers roomUsers = mongoTemplate.findOne(query, RoomUsers.class, COLLECTION_ROOMS_USERS);
        return roomUsers;
    }

    public Room updateRoom(List<String> keys, Room room) {
        Query query = mongoUtil.getQuery("roomId", room.getRoomId());
        Update update = mongoUtil.getUpdate();
        for (String key: keys) {
            Object updateValue = getUpdateValue(room, key);
            if (!ObjectUtils.isEmpty(updateValue)) {
                update.set(key, updateValue);
            }
        }
        return mongoTemplate.findAndModify(query, update, Room.class, COLLECTION_ROOMS);
    }
    private Object getUpdateValue(Room room, String key){
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(room.getClass(), key);
        Method readMethod = pd.getReadMethod();
        try {
            return readMethod.invoke(room);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateRoomsUserInfo(Room room) {
        Query query = mongoUtil.getQuery("roomId", room.getRoomId());
        Update update = mongoUtil.getUpdate();
        update.set("users",room.getUsers());
        update.set("userCount",room.getUserCount());
        mongoTemplate.upsert(query, update, COLLECTION_ROOMS);
    }

    public List<Message> getUnReadMessages(String roomId, Friend friend) {
        Query query = mongoUtil.getQuery("roomId", roomId);
        RoomMesages roomMessages = mongoTemplate.findOne(query, RoomMesages.class, COLLECTION_ROOMS_MESSAGES);
        int lastReadMessageIndex = 0;

        for (Message message: roomMessages.getMessages()) {
            if (message.getTimestamp() <= friend.getLastMessageReadTimestamp()) {
                break;
            }
            lastReadMessageIndex++;
        }
        return new ArrayList(roomMessages.getMessages().subList(0,lastReadMessageIndex));
    }

    public long getLastMessageReadTimestamp(String roomId) {
        final String minAlias = "min";
        MatchOperation matchOperation = Aggregation.match(Criteria.where("roomId").is(roomId));
        ProjectionOperation projectionOperation = Aggregation.project()
                .and(AccumulatorOperators.valueOf("users.lastMessageReadTimestamp").min())
                .as(minAlias);
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectionOperation
        );
        Map uniqueMappedResult = mongoTemplate.aggregate(aggregation, COLLECTION_ROOMS_USERS, Map.class).getUniqueMappedResult();
        long lastMessageReadTimestamp = System.currentTimeMillis();
        if (!ObjectUtils.isEmpty(uniqueMappedResult)) {
            lastMessageReadTimestamp = (long) uniqueMappedResult.get(minAlias);
        }
        return lastMessageReadTimestamp;
    }

    public void deleteMessageBeforeLastMessageReadTimestamp(String roomId, long lastMessageReadTimestamp) {
        Query query = mongoUtil.getQuery("roomId", roomId);
        //비교값 넣기 subkey
        String pullQuery = "{'timestamp':{$lt:"+lastMessageReadTimestamp+"}}";
        Update updatePull = mongoUtil.updatePull("messages", BasicDBObject.parse(pullQuery));

        UpdateResult updateResult = mongoTemplate.updateMulti(query, updatePull, COLLECTION_ROOMS_MESSAGES);
        System.out.println("delete message count::::: "+updateResult.getModifiedCount());
    }

    public RoomMesages minusUnReadCount(String roomId, String[] chatIds) {
        Query query = mongoUtil.getQuery("roomId", roomId);
        Update update = mongoUtil.getUpdate();
        update.inc("messages.$[e].unReadCount", -1)
                .filterArray(Criteria.where("e.chatId").in(chatIds));
        return mongoTemplate.findAndModify(query, update, RoomMesages.class, COLLECTION_ROOMS_MESSAGES);
    }

    public void updateChatRoomUserLastMessageReadTimestamp(String roomId, String userId) {
        Criteria criteria = Criteria
                .where("roomId").is(roomId)
                .andOperator(Criteria.where("users")
                        .elemMatch(Criteria.where("_id").is(userId)));
        Query query = mongoUtil.getQuery(criteria);
        Update update = mongoUtil.getUpdate();
        update.set("users.$.lastMessageReadTimestamp", System.currentTimeMillis());
        mongoTemplate.updateFirst(query, update, COLLECTION_ROOMS_USERS);
    }

    public void addSearchHistory(SearchHistory searchHistory){
        mongoTemplate.save(searchHistory, COLLECTION_HISTORY_SEARCH);
    }

    public List<Room> findRoomsByKeyword(String keyword) {
        List<Room> rooms = new ArrayList<>();
        rooms.addAll(findRoomsByTitle(keyword));
        rooms.addAll(findRoomsByTag(keyword));
        //중복제거 후 리턴
        return rooms.stream().distinct().collect(Collectors.toList());
    }

    private List<Room> findRoomsByTag(String keyword) {
        Query query = mongoUtil.getQuery("roomType", RoomType.OPEN_CHAT.toString())
                .addCriteria(Criteria.where("searchPermit").is(true)
                .and("tags").is(keyword));
        List<Room> rooms = mongoTemplate.find(query, Room.class, COLLECTION_ROOMS);
        return rooms;
    }

    private List<Room> findRoomsByTitle(String keyword) {
        Query query = mongoUtil.getQuery("roomType", RoomType.OPEN_CHAT.toString())
                .addCriteria(Criteria.where("searchPermit").is(true)
                        .and("title").regex(keyword));
        List<Room> rooms = mongoTemplate.find(query, Room.class, COLLECTION_ROOMS);
        return rooms;
    }


}
