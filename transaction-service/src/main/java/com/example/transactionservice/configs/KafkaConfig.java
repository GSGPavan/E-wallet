package com.example.transactionservice.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.bootstrap-server}")
    String bootstrapServer;

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        Map<String,Object> producerProperties = new HashMap<>();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String,String> consumerFactory(){
        Map<String,Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }
}
