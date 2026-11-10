package com.example.climentostask1.controller;

import com.example.climentostask1.entity.User;

import com.example.climentostask1.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {

        this.service = service;

    }


    // SIGNUP

    @PostMapping("/signup")
    public Map<String, String> signup(
            @RequestBody User user) {

        return Map.of(
                "message",
                service.signup(user)
        );

    }


    // LOGIN

    @PostMapping("/login")
    public Map<String, String> login(

            @RequestBody Map<String, String> data,

            HttpSession session) {

        User user = service.login(

                data.get("username"),

                data.get("password")

        );

        session.setAttribute("userId", user.getId());

        session.setAttribute("userName", user.getName());

        return Map.of(

                "message", "Login successful",

                "name", user.getName()

        );

    }


    // CHECK SESSION

    @GetMapping("/session")
    public Map<String, Object> checkSession(
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please login"
            );

        }

        return Map.of(

                "loggedIn", true,

                "name", session.getAttribute("userName")

        );

    }


    // RESET PASSWORD

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(

            @RequestBody Map<String, String> data) {

        return Map.of(

                "message",

                service.resetPassword(

                        data.get("email"),

                        data.get("password")

                )

        );

    }


    // LOGOUT

    @PostMapping("/logout")
    public Map<String, String> logout(
            HttpSession session) {

        session.invalidate();

        return Map.of(

                "message", "Logout successful"

        );

    }

}
