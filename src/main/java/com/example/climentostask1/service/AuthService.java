package com.example.climentostask1.service;

import com.example.climentostask1.entity.User;
import com.example.climentostask1.entity.PasswordResetToken;

import com.example.climentostask1.repository.UserRepository;
import com.example.climentostask1.repository.PasswordResetTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;

    @Value("${app.reset-url}")
    private String resetUrl;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public AuthService(
            UserRepository repository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder encoder,
            JavaMailSender mailSender) {

        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.encoder = encoder;
        this.mailSender = mailSender;
    }

    // SIGNUP

    public String signup(User user) {

        if (repository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        if (repository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
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
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password"
                        )
                );

        if (!encoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        return user;
    }

    // FORGOT PASSWORD

    public String forgotPassword(String email) {

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Email is not registered"
                        )
                );

        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(token);
        resetToken.setUserId(user.getId());

        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );

        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        String link = resetUrl + "?token=" + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(mailUsername);
        message.setTo(user.getEmail());

        message.setSubject(
                "Climentos - Password Reset Request"
        );

        message.setText(
                "Hello " + user.getName() + ",\n\n" +
                        "Click the below link to reset your password:\n\n" +
                        link + "\n\n" +
                        "This link expires in 15 minutes.\n\n" +
                        "Regards,\nClimentos Technologies"
        );

        mailSender.send(message);

        return "Password reset link sent successfully";
    }

    // RESET PASSWORD

    public String resetPassword(
            String token,
            String password) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid reset link"
                                )
                        );

        if (resetToken.isUsed()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reset link already used"
            );
        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reset link expired"
            );
        }

        User user = repository.findById(
                resetToken.getUserId()
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                )
        );

        user.setPassword(
                encoder.encode(password)
        );

        repository.save(user);

        resetToken.setUsed(true);

        tokenRepository.save(resetToken);

        return "Password updated successfully";
    }
}














