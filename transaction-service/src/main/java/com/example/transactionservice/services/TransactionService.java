package com.example.transactionservice.services;

import com.example.transactionservice.models.Transaction;
import com.example.transactionservice.repositories.TransactionCacheRepository;
import com.example.transactionservice.repositories.TransactionRepository;
import com.example.transactionservice.services.Constants.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionCacheRepository transactionCacheRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public String createTransaction(Transaction transaction) throws Exception {
        Transaction dbTransaction;
        if(transaction!=null)
        {
            dbTransaction = transactionRepository.save(transaction);
            if(dbTransaction!=null)
            {
                transactionCacheRepository.set(getKeyForTransaction(transaction.getExternalId()),objectMapper.writeValueAsString(dbTransaction));
                sendCreatedTransactionToTopic(dbTransaction);
                return dbTransaction.getExternalId();
            }
        }
        throw new Exception("transaction can not be null");
    }

    private void sendCreatedTransactionToTopic(Transaction dbTransaction) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("senderId",dbTransaction.getSenderId());
        jsonObject.accumulate("receiverId", dbTransaction.getReceiverId());
        jsonObject.accumulate("amount",dbTransaction.getAmount());
        jsonObject.accumulate("externalId",dbTransaction.getExternalId());
        kafkaTemplate.send(Constant.TRANSACTION_CREATED_TOPIC,jsonObject.toString());
    }

//    @KafkaListener(topics = {Constant.WALLET_UPDATED_TOPIC},groupId = "walletUpdatedConsumerGroup")
//    public void updateTransaction(String msg){
//
//    }

    public String getKeyForTransaction(String externalId){
        return Constant.REDIS_TRANSACTION_KEY_DELIMITER+externalId;
    }
}
