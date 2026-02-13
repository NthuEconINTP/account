package com.moneyflow.account.auth.service;

import com.moneyflow.account.auth.entity.User;
import com.moneyflow.account.auth.repository.UserRepository;
import com.moneyflow.account.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ===== 登入 =====
    public String login(String username, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new Exception("User not found");
        }

        User user = optionalUser.get();

        if (!user.getIsActive()) {
            throw new Exception("User is not active");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }

        // 呼叫 JwtUtil 生成 token
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    // ===== 註冊 =====
    public User register(String username, String rawPassword, String email, String role) throws Exception {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setIsActive(true);
        user.setRole(role != null ? role : "USER");

        return userRepository.save(user);
    }

    // ===== 密碼加密工具 =====
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
