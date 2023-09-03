package com.example.transactionservice.repositories;

import com.example.transactionservice.models.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionCacheRepository {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;
    public void set(String transactionKey, String transaction) throws JsonProcessingException {
        if(transactionKey!=null && transaction!=null)
            redisTemplate.opsForValue().set(transactionKey,objectMapper.writeValueAsString(transaction));
    }
}
