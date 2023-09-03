package com.example.transactionservice.controllers;

import com.example.transactionservice.dtos.CreateTransactionRequest;
import com.example.transactionservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/createTransaction")
    public String createTransaction(@RequestBody @Valid CreateTransactionRequest createTransactionRequest) throws Exception {
        if(createTransactionRequest!=null)
        {
           return transactionService.createTransaction(createTransactionRequest.to());
        }
        throw new Exception("Bad Request");
    }
}
