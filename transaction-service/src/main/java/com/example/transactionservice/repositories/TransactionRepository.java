package com.example.transactionservice.repositories;

import com.example.transactionservice.models.Transaction;
import com.example.transactionservice.models.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Query("select t from Transaction t where t.externalId=:externalId")
    Transaction getTransaction(String externalId);

    @Modifying
    @Query("update Transaction t set t.status=:transactionStatus where t.externalId=:externalId")
    int updateTransactionStatus(String externalId, TransactionStatus transactionStatus);

    @Query(value = "select * from transaction as t where (t.sender_id=?3 or t.receiver_id=?3) order by created_on desc limit ?1 offset ?2",nativeQuery = true)
    List<Transaction> transactionList(Integer pageSize, Integer pageStart, String phoneNumber);

    @Query("select count(t) from Transaction t where (t.senderId=:phoneNumber or t.receiverId=:phoneNumber)")
    int transactionCount(String phoneNumber);
}
