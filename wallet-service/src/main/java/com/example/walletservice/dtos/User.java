package com.example.walletservice.dtos;


import lombok.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    private Integer id;

    private String name;

    private String phoneNumber;

    private String email;

    private Integer age;

    private Date createdOn;

    private Date updatedOn;
}

