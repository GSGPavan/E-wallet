package com.example.transactionservice.services;

import com.example.transactionservice.models.Transaction;
import com.example.transactionservice.models.TransactionStatus;
import com.example.transactionservice.repositories.TransactionCacheRepository;
import com.example.transactionservice.repositories.TransactionRepository;
import com.example.transactionservice.services.constants.Constant;
import com.example.transactionservice.services.constants.WalletUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;

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

    @Autowired
    RestTemplate restTemplate;

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

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class, Throwable.class})
    @KafkaListener(topics = {Constant.WALLET_UPDATED_TOPIC},groupId = "walletUpdatedConsumerGroup")
    public void updateTransaction(String msg) throws Exception {
        JSONObject jsonObject = objectMapper.convertValue(msg, JSONObject.class);
        if(jsonObject!=null)
        {
            String externalId = jsonObject.getString("externalId");
            String walletUpdate = jsonObject.getString("walletUpdate");
            String senderId = jsonObject.getString("senderId");
            String receiverId = jsonObject.getString("receiverId");
            Long amount = jsonObject.getLong("amount");
            String apiUrl = "http://localhost:8080/getUserDetails?phoneNumber=";
            String senderEmail = null;
            String receiverEmail = null;
            ResponseEntity<String> senderResponseEntity = restTemplate.getForEntity(apiUrl+senderId,String.class);
            if(senderResponseEntity.getStatusCode()== HttpStatus.OK)
            {
                String senderString = senderResponseEntity.getBody();
                if(senderString!=null)
                {
                    JSONObject senderJson = objectMapper.convertValue(senderString,JSONObject.class);
                    senderEmail = senderJson.getString("email");
                }
            }
            ResponseEntity<String> receiverResponseEntity = restTemplate.getForEntity(apiUrl+receiverId,String.class);
            if(receiverResponseEntity.getStatusCode()== HttpStatus.OK)
            {
                String receiverString = receiverResponseEntity.getBody();
                if(receiverString!=null)
                {
                    JSONObject receiverJson = objectMapper.convertValue(receiverString, JSONObject.class);
                    receiverEmail = receiverJson.getString("email");
                }
            }
            int count;
            JSONObject transactionUpdatedJson = getTransactionUpdatedJson(externalId,amount,senderEmail,receiverEmail,senderId,receiverId);
            if(walletUpdate.equals(WalletUpdate.SUCCESS.toString()))
            {
                count = transactionRepository.updateTransactionStatus(externalId, TransactionStatus.SUCCESS);
                transactionUpdatedJson.accumulate("transactionUpdate", TransactionStatus.SUCCESS);
            }
            else
            {
                count = transactionRepository.updateTransactionStatus(externalId, TransactionStatus.FAILURE);
                transactionUpdatedJson.accumulate("transactionUpdate", TransactionStatus.FAILURE);
            }
            if(count == 1)
            {
                Transaction updatedTransaction = transactionRepository.getTransaction(externalId);
                transactionCacheRepository.set(getKeyForTransaction(externalId),objectMapper.writeValueAsString(updatedTransaction));
                kafkaTemplate.send(Constant.TRANSACTION_UPDATED_TOPIC,transactionUpdatedJson.toString());
            }
            else if(count == 0)
            {
                throw new Exception("Transaction is not present in system");
            }
            else if(count>1)
            {
                throw new Exception("There are multiple transactions with same external id in the system.So rolling back");
            }
        }
    }

    private JSONObject getTransactionUpdatedJson(String externalId, Long amount, String senderEmail, String receiverEmail, String senderId, String receiverId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("externalId", externalId);
        jsonObject.accumulate("amount", amount);
        jsonObject.accumulate("senderEmail",senderEmail);
        jsonObject.accumulate("receiverEmail",receiverEmail);
        jsonObject.accumulate("senderId",senderId);
        jsonObject.accumulate("receiverId",receiverId);
        return jsonObject;
    }

    public String getKeyForTransaction(String externalId){
        return Constant.REDIS_TRANSACTION_KEY_DELIMITER+externalId;
    }

    public List<Transaction> transactionList(Integer pageSize, Integer pageStart, String phoneNumber) throws Exception {
        if(pageSize!=null && pageSize!=0 && pageStart!=null && phoneNumber!=null)
        {
             return transactionRepository.transactionList(pageSize,pageStart,phoneNumber);
        }
        throw new Exception("Invalid request parameters");
    }

    public int transactionCount(String phoneNumber) throws Exception {
        if(phoneNumber!=null)
        {
            return transactionRepository.transactionCount(phoneNumber);
        }
        throw new Exception("phone number cannot be null");
    }
}
