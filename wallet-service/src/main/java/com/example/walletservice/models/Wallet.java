package com.example.walletservice.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Wallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true,nullable = false,length = 10)
    private String phoneNumber;

    private long balance; //least denomination i.e paise

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;
}
