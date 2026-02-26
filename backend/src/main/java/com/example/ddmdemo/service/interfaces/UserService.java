package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public User getUserByEmail(String email);
}
