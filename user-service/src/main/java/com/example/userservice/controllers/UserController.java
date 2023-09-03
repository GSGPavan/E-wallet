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
    public void createUser(@RequestBody @Valid  CreateUserRequest createUserRequest) throws JsonProcessingException {
        userService.createUser(createUserRequest.toUser());
    }

    @PostMapping("/updateUser")
    public void updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) throws JsonProcessingException {
        userService.updateUser(updateUserRequest.toUser());
    }

    @GetMapping("/getUserDetails/id")
    public User getUserDetails(@RequestParam Integer id) throws Exception {
        return userService.getUserDetails(id);
    }

}
