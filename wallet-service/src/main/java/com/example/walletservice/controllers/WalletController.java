package com.example.walletservice.controllers;

import com.example.walletservice.dtos.WalletResponse;
import com.example.walletservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping(value = {"/getWallet"})
    public WalletResponse getWallet(@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) Integer walletId) throws Exception {
        if(phoneNumber == null && walletId == null)
            throw new Exception("At least one parameter should be given in request");
        else
            return walletService.getWallet(phoneNumber, walletId);
    }

    @PostMapping("/addMoney")
    public boolean addMoneyToWallet(@RequestParam String phoneNumber, @RequestParam Long amount) throws Exception {
        if(phoneNumber!=null && amount!=null && amount!=0L)
        {
            return walletService.addMoneyToWallet(phoneNumber,amount);
        }
        throw new Exception("phoneNumber and amount cannot be null");
    }

}
