package com.example.userservice.repositories;

import com.example.userservice.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserCacheRepository {

    @Autowired
    RedisTemplate<String,String> redisTemp;

    @Autowired
    ObjectMapper objectMapper;

    public void  set(String stringKeyForUser, String user) {
        redisTemp.opsForValue().set(stringKeyForUser, user);
    }

    public String get(String stringKeyForUser) {
        return redisTemp.opsForValue().get(stringKeyForUser);

    }
}
