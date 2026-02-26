package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.dto.LoginDTO;
import com.example.ddmdemo.model.User;
import com.example.ddmdemo.respository.UserRepository;
import com.example.ddmdemo.security.TokenUtil;
import com.example.ddmdemo.service.interfaces.AuthService;
import com.example.ddmdemo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenUtil tokenUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public String login(LoginDTO loginDto) {
        // 1. Pronađi merchanta u bazi
        User user = userService.getUserByEmail(loginDto.getEmail());
        if (user == null) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // 2. Proveri lozinku (hash vs plain text)
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // 3. Generiši token pomoću tvoje TokenUtil klase
        return tokenUtil.generateToken(user);
    }
}
