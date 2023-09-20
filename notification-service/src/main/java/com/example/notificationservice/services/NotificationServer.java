package com.example.notificationservice.services;

import com.example.notificationservice.services.constants.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;


    @KafkaListener(topics = {Constant.WALLET_CREATED_TOPIC},groupId = "walletCreatedConsumerGroup")
    public void walletCreated(String msg)
    {
        if(msg!=null) {
            JSONObject jsonObject = objectMapper.convertValue(msg, JSONObject.class);
            String email = jsonObject.getString("email");
            String phoneNumber = jsonObject.getString("phoneNumber");
            Long amount = jsonObject.getLong("amount");
            String content = walletCreatedMessage(phoneNumber, amount);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setFrom("gsgpavan228@gmail.com");
            simpleMailMessage.setSubject("Welcome to E-Wallet");
            simpleMailMessage.setText(content);
            javaMailSender.send(simpleMailMessage);
        }

    }

    @KafkaListener(topics = {Constant.TRANSACTION_UPDATED_TOPIC},groupId = "transactionUpdatedConsumerGroup")
    public void transactionUpdated(String msg)
    {
        JSONObject jsonObject = objectMapper.convertValue(msg, JSONObject.class);
        String externalId = jsonObject.getString("externalId");
        Long amount = jsonObject.getLong("amount");
        String senderId = jsonObject.getString("senderId");
        String receiverId = jsonObject.getString("receiverId");
        String senderEmail = jsonObject.getString("senderEmail");
        String receiverEmail = jsonObject.getString("receiverEmail");
        String transactionUpdate = jsonObject.getString("transactionUpdate");
        String senderContent = senderMessage(externalId,amount,senderId,receiverId,transactionUpdate);
        String receiverContent = receiverMessage(externalId,amount,senderId,receiverId,transactionUpdate);

        if(senderContent != null && senderContent.length() > 0){
            simpleMailMessage.setTo(senderEmail);
            simpleMailMessage.setSubject("E-Wallet Transaction Updates");
            simpleMailMessage.setFrom("ewallet.jbdl50@gmail.com");
            simpleMailMessage.setText(senderContent);
            javaMailSender.send(simpleMailMessage);
        }

        if(receiverContent != null && receiverContent.length() > 0){
            simpleMailMessage.setTo(receiverEmail);
            simpleMailMessage.setSubject("E-Wallet Transaction Updates");
            simpleMailMessage.setFrom("ewallet.jbdl50@gmail.com");
            simpleMailMessage.setText(receiverContent);
            javaMailSender.send(simpleMailMessage);
        }

    }

    private String receiverMessage(String externalId, Long amount, String senderId, String receiverId, String transactionUpdate) {
        String reciverMessage = "";
        if(transactionUpdate.equals("SUCCESS"))
        {
            reciverMessage = "Hi "+receiverId+", Your wallet has been credited with Rs."+amount/100+" by "+senderId+" under transaction of id "+externalId;
        }
        return reciverMessage;
    }

    private String senderMessage(String externalId, Long amount, String senderId, String receiverId, String transactionUpdate) {
        String senderMessage = "";
        if(transactionUpdate.equals("SUCCESS"))
        {
            senderMessage = "Hi "+senderId+", Your transaction "+externalId+" of amount Rs."+amount/100+" has been credited to "+receiverId;
        }
        else
        {
            senderMessage = "Hi "+senderId+", Your transaction "+externalId+" of amount Rs."+amount/100+" to "+receiverId +" is failed. Please try again after some time.";
        }
        return senderMessage;
    }

    public String walletCreatedMessage(String phoneNumber, Long amount)
    {
        return "Hi, Wallet is created for "+phoneNumber+". An amount of "+amount/100+" rupees is credited into your wallet. Have a nice day...";
    }



}
