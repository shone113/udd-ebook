package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.model.User;
import com.example.ddmdemo.respository.UserRepository;
import com.example.ddmdemo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
