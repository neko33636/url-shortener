package com.maksim.urlshortener.service;

import com.maksim.urlshortener.entity.Role;
import com.maksim.urlshortener.entity.UserEntity;
import com.maksim.urlshortener.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserEntity register(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя обязательно");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль обязателен");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь уже существует");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username.trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(Role.READ_WRITE);

        return userRepository.save(user);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}
