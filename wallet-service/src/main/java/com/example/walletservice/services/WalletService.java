package com.example.walletservice.services;

import com.example.walletservice.dtos.User;
import com.example.walletservice.dtos.WalletResponse;
import com.example.walletservice.models.Wallet;
import com.example.walletservice.repositories.WalletCacheRepository;
import com.example.walletservice.repositories.WalletRepository;
import com.example.walletservice.services.constants.Constant;
import com.example.walletservice.services.constants.WalletUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class WalletService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletCacheRepository walletCacheRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @KafkaListener(topics = {Constant.USER_CREATED_TOPIC},groupId = "userCreatedConsumerGroup")
    public void createWallet(String stringUser) throws JsonProcessingException {
        if(stringUser!=null)
        {
            User user = objectMapper.readValue(stringUser,User.class);
            if(user!=null) {
                Wallet wallet = Wallet.builder()
                        .phoneNumber(user.getPhoneNumber())
                        .balance(Constant.WALLET_INITIAL_BALANCE).build();
                Wallet dbWallet = walletRepository.save(wallet);
                walletCacheRepository.save(getKeyForWallet(dbWallet.getId()),dbWallet);
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email",user.getEmail());
                jsonObject.accumulate("phoneNumber",user.getPhoneNumber());
                jsonObject.accumulate("amount",dbWallet.getBalance());
                kafkaTemplate.send(Constant.WALLET_CREATED_TOPIC,jsonObject.toString());
            }
        }
    }

    public String getKeyForWallet(int walletId)
    {
        return Constant.REDIS_WALLET_KEY_DELIMITER+walletId;
    }

    public WalletResponse getWallet(String phoneNumber, Integer walletId) throws Exception {
        WalletResponse walletResponse;
        if(walletId != null)
        {
            Wallet wallet= walletCacheRepository.get(getKeyForWallet(walletId));
            if(wallet==null)
            {
                walletResponse = walletRepository.findByWalletId(walletId);
                if(walletResponse==null)
                {
                    throw new Exception("Wallet not found");
                }
                return walletResponse;
            }
            return WalletResponse.builder()
                    .id(wallet.getId())
                    .balance(wallet.getBalance())
                    .phoneNumber(wallet.getPhoneNumber())
                    .build();
        }
        else if(phoneNumber != null)
        {
            walletResponse = walletRepository.findByPhoneNumber(phoneNumber);
            if(walletResponse == null)
            {
                throw new Exception("Wallet not found");
            }
            return walletResponse;
        }
        throw new Exception("At least one parameter should be given");
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class, Throwable.class})
    @KafkaListener(topics = {Constant.TRANSACTION_CREATED_TOPIC},groupId = "transactionCreatedConsumerGroup")
    public void updateWallet(String msg) throws Exception {
        JSONObject jsonObject = objectMapper.convertValue(msg,JSONObject.class);
        if(jsonObject!=null)
        {
            String senderId = jsonObject.getString("senderId");
            String receiverId = jsonObject.getString("receiverId");
            long amount = jsonObject.getLong("amount");
            String externalId = jsonObject.getString("externalId");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.accumulate("externalId",externalId);
            jsonObject1.accumulate("senderId", senderId);
            jsonObject1.accumulate("receiverId", receiverId);
            jsonObject1.accumulate("amount", amount);
            Wallet senderWallet = walletRepository.findWalletByPhoneNumber(senderId);
            Wallet receiverWallet= walletRepository.findWalletByPhoneNumber(receiverId);
            if(senderWallet==null || receiverWallet == null)
            {
                jsonObject1.accumulate("walletUpdate", WalletUpdate.FAILED);
                kafkaTemplate.send(Constant.WALLET_UPDATED_TOPIC,jsonObject1.toString());
                throw new Exception("Sender or Receiver does not exist in system");
            }
            if(senderWallet.getBalance()<amount)
            {
                jsonObject1.accumulate("walletUpdate", WalletUpdate.FAILED);
                kafkaTemplate.send(Constant.WALLET_UPDATED_TOPIC,jsonObject1.toString());
                throw new Exception("Insufficient balance");
            }
            int senderCount = walletRepository.updateAmount(senderId,-amount);
            int receiverCount = walletRepository.updateAmount(receiverId,amount);
            if(senderCount != 1 || receiverCount != 1)
            {
                jsonObject1.accumulate("walletUpdate", WalletUpdate.FAILED);
                kafkaTemplate.send(Constant.WALLET_UPDATED_TOPIC,jsonObject1.toString());
                throw new Exception("sender or receiver does not exist");
            }
            else
            {
                walletCacheRepository.save(getKeyForWallet(senderWallet.getId()),senderWallet);
                walletCacheRepository.save(getKeyForWallet(receiverWallet.getId()),receiverWallet);
                jsonObject1.accumulate("walletUpdate", WalletUpdate.SUCCESS);
                kafkaTemplate.send(Constant.WALLET_UPDATED_TOPIC,jsonObject1.toString());
            }
        }
    }

    @Transactional(rollbackOn = {Exception.class,Throwable.class, RuntimeException.class})
    public boolean addMoneyToWallet(String phoneNumber, Long amount) throws Exception {
        if(phoneNumber!=null && amount!=null && amount!=0L)
        {
            return walletRepository.updateAmount(phoneNumber,amount) == 1;
        }
        throw new Exception("phoneNumber and amount cannot be null");
    }
}
