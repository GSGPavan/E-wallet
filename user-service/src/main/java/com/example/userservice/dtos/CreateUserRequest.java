package com.example.userservice.dtos;

import com.example.userservice.models.User;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateUserRequest {

    @NotBlank
    private String name;

    @NotBlank @Size(min = 10,max = 10)
    private String phoneNumber;

    private String email;

    @Min(18)
    private int age;

    public User toUser()
    {
        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .age(age)
                .build();
    }
}
