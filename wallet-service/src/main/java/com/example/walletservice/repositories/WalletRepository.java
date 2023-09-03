package com.example.walletservice.repositories;

import com.example.walletservice.dtos.WalletResponse;
import com.example.walletservice.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    //@Query("select * from wallet w where w.phoneNumber=:phoneNumber")//sql query
    @Query("select new WalletResponse(w.id, w.phoneNumber,w.balance) from Wallet w where w.phoneNumber=:phoneNumber")//jpql
    WalletResponse findByPhoneNumber(String phoneNumber);

    @Query("select w from Wallet w where w.phoneNumber=:phoneNumber")//jpql
    Wallet findWalletByPhoneNumber(String phoneNumber);

    @Query("select new WalletResponse(w.id, w.phoneNumber,w.balance) from Wallet w where w.id=:walletId")//jpql
    WalletResponse findByWalletId(Integer walletId);

    @Modifying
    @Query("update Wallet w set w.balance=w.balance+:amount where phoneNumber=:phoneNumber")
    int updateAmount(String phoneNumber, Long amount);

}
