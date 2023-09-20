package com.example.transactionservice.controllers;

import com.example.transactionservice.dtos.CreateTransactionRequest;
import com.example.transactionservice.models.Transaction;
import com.example.transactionservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/transactionCount")
    public int transactionCount(@RequestParam String phoneNumber) throws Exception {
        if(phoneNumber!=null)
        {
            return transactionService.transactionCount(phoneNumber);
        }
        throw new Exception("phone number cannot be null");
    }

    @GetMapping("/transactionList")
    public List<Transaction> transactionList(@RequestParam Integer pageSize, @RequestParam Integer pageStart, @RequestParam String phoneNumber) throws Exception {
        if(pageSize!=null && pageSize!=0 && pageStart!=null && phoneNumber!=null) {
            return transactionService.transactionList(pageSize,pageStart,phoneNumber);
        }
        throw new Exception("pageSize, pageStart, phoneNumber can not be null");
    }


}
