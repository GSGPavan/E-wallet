package com.example.transactionservice.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String externalId;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private String senderId; //sender phone number

    @Column(nullable = false)
    private String receiverId; //receiver phone number

    @Enumerated(value = EnumType.ORDINAL)
    private TransactionStatus status;

    private String reason;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;
}
