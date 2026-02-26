package com.example.ddmdemo.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordHasher {
    private static final int COST = 12;

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        System.out.println("Input plain password: '" + plainPassword + "'");
        System.out.println("Stored hashed password: '" + hashedPassword + "'");
        System.out.println("My hashed password '" + hashPassword(plainPassword));
        System.out.println("COMPARATION RESULT: '" + BCrypt.checkpw(plainPassword, hashedPassword));

        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
