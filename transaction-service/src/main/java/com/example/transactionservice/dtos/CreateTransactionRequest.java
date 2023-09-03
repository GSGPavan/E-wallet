package com.example.transactionservice.dtos;

import com.example.transactionservice.models.Transaction;
import com.example.transactionservice.models.TransactionStatus;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateTransactionRequest {

    @Min(1)
    private long amount;

    @NotBlank @Size(min = 10,max = 10)
    private String senderId; //sender phone number

    @NotBlank @Size(min = 10,max = 10)
    private String receiverId; //receiver phone number

    private String reason;

    public Transaction to(){
        return Transaction.builder()
                .externalId(UUID.randomUUID().toString())
                .amount(getAmount())
                .senderId(getSenderId())
                .receiverId(getReceiverId())
                .status(TransactionStatus.PENDING)
                .reason(getReason())
                .build();
    }
}
