package com.example.userservice.services;

import com.example.userservice.models.User;
import com.example.userservice.repositories.UserCacheRepository;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.services.constants.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static final String STRING_DELIMITER = "User::";
    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserCacheRepository userCacheRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public User createUser(User user) throws Exception {
        User DBUser;
        if(user!=null) {
            DBUser = userRepository.save(user);
            userCacheRepository.set(getKeyForUser(DBUser), objectMapper.writeValueAsString(DBUser));
            kafkaTemplate.send(Constant.USER_CREATED_TOPIC,objectMapper.writeValueAsString(DBUser));
            return DBUser;
        }
        throw new Exception("User cannot be null");
    }

    public String getKeyForUser(User user)
    {
        return STRING_DELIMITER+user.getId();
    }

    public String getKeyForUser(int id)
    {
        return STRING_DELIMITER+id;
    }

    public User getUserDetails(Integer id, String phoneNumber) throws Exception {
        User DBUser;
        if(id!=null) {
            User cacheUser = objectMapper.readValue(userCacheRepository.get(getKeyForUser(id)), User.class);
            DBUser = cacheUser;
            if (cacheUser == null) {
                DBUser = userRepository.findById(id).orElse(null);
                if (DBUser != null) {
                    userCacheRepository.set(getKeyForUser(DBUser), objectMapper.writeValueAsString(DBUser));
                } else throw new Exception("No user present with the given userid");
            }
            return DBUser;
        }
        DBUser = userRepository.findByPhoneNumber(phoneNumber);
        if(DBUser!=null)
        {
            return DBUser;
        }
        throw new Exception("No user present with the given phoneNumber");
    }

    public User  updateUser(User user) throws Exception {
        User DBUser;
        if(user != null) {
            User savedUser = userRepository.findByPhoneNumber(user.getPhoneNumber());
            savedUser.setAge(user.getAge());
            savedUser.setName(user.getName());
            savedUser.setEmail(user.getEmail());
            DBUser = userRepository.save(savedUser);
            userCacheRepository.set(getKeyForUser(savedUser), objectMapper.writeValueAsString(DBUser));
            return DBUser;
        }
        throw new Exception("User cannot be null");
    }
}
