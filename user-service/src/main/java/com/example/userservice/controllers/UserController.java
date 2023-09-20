package com.example.userservice.controllers;

import com.example.userservice.dtos.CreateUserRequest;
import com.example.userservice.dtos.UpdateUserRequest;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/createUser")
    public User createUser(@RequestBody @Valid  CreateUserRequest createUserRequest) throws Exception {
        return userService.createUser(createUserRequest.toUser());
    }

    @PostMapping("/updateUser")
    public User updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) throws Exception {
        return userService.updateUser(updateUserRequest.toUser());
    }

    @GetMapping("/getUserDetails")
    public User getUserDetails(@RequestParam(required = false) Integer id, @RequestParam(required = false) String phoneNumber) throws Exception {
        return userService.getUserDetails(id,phoneNumber);
    }

}
