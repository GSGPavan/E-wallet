package com.example.walletservice.services;

import com.example.walletservice.dtos.User;
import com.example.walletservice.dtos.WalletResponse;
import com.example.walletservice.models.Wallet;
import com.example.walletservice.repositories.WalletCacheRepository;
import com.example.walletservice.repositories.WalletRepository;
import com.example.walletservice.services.constants.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @KafkaListener(topics = {Constant.USER_CREATED_TOPIC},groupId = "userConsumerGroup")
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
                kafkaTemplate.send(Constant.WALLET_CREATED_TOPIC,objectMapper.writeValueAsString(dbWallet));
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

    @Transactional
    @KafkaListener(topics = {Constant.TRANSACTION_CREATED_TOPIC})
    public void updateWallet(String msg){



    }

}
