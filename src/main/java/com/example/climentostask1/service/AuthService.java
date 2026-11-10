package com.example.climentostask1.service;

import com.example.climentostask1.entity.User;

import com.example.climentostask1.repository.UserRepository;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;


    public AuthService(
            UserRepository repository,
            PasswordEncoder encoder) {

        this.repository = repository;

        this.encoder = encoder;

    }


    // SIGNUP

    public String signup(User user) {

        if (repository.existsByEmail(user.getEmail())) {

            throw new RuntimeException("Email already exists");

        }

        if (repository.existsByUsername(user.getUsername())) {

            throw new RuntimeException("Username already exists");

        }

        user.setPassword(
                encoder.encode(user.getPassword())
        );

        repository.save(user);

        return "Account created successfully";

    }


    // LOGIN

    public User login(String username, String password) {

        User user = repository.findByUsername(username)

                .or(() -> repository.findByEmail(username))

                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"
                        )
                );


        if (!encoder.matches(password, user.getPassword())) {

            throw new RuntimeException(
                    "Invalid username or password"
            );

        }

        return user;

    }


    // RESET PASSWORD

    public String resetPassword(String email, String password) {

        User user = repository.findByEmail(email)

                .orElseThrow(() ->
                        new RuntimeException("Email not found")
                );

        user.setPassword(
                encoder.encode(password)
        );

        repository.save(user);

        return "Password reset successfully";

    }

}


