package com.example.transactionservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class Config {
    @Bean
    public ObjectMapper objectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}
