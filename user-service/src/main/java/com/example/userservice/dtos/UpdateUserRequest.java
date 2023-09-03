package com.example.userservice.dtos;

import com.example.userservice.models.User;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateUserRequest {
    @NotBlank
    private String name;

    private String email;

    @NotBlank
    private String phoneNumber;

    @Min(18)
    private int age;

    public User toUser()
    {
        return User.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .age(age)
                .build();
    }

}
