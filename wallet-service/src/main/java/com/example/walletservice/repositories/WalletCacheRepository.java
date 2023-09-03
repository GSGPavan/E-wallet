package com.example.walletservice.repositories;

import com.example.walletservice.dtos.WalletResponse;
import com.example.walletservice.models.Wallet;
import com.example.walletservice.services.constants.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class WalletCacheRepository {

    @Autowired
    RedisTemplate<String, Wallet> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void save(String key, Wallet wallet) {
        Map walletMap = objectMapper.convertValue(wallet, Map.class);
        redisTemplate.opsForHash().putAll(key,walletMap);
    }

    public Wallet get(String key){
        Map m = redisTemplate.opsForHash().entries(key);
        if(m != null) {
            return objectMapper.convertValue(m, Wallet.class);
        }
        return null;
    }

}
