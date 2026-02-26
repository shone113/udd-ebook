package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.LoginDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    public String login(LoginDTO loginDTO);
}
