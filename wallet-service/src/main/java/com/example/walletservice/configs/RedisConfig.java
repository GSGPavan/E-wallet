package com.example.walletservice.configs;

import com.example.walletservice.models.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    String redisHost;

    @Value("${redis.port}")
    int redisPort;

    @Value("${redis.username}")
    String redisUsername;

    @Value("${redis.password}")
    String redisPassword;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost,redisPort);
       // redisStandaloneConfiguration.setUsername(redisUsername);
        //redisStandaloneConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Wallet> redisTemplate(){
        RedisTemplate<String,Wallet> redisTemplate= new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());

        return redisTemplate;
    }
}
