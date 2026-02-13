package com.moneyflow.account.auth.controller;

import com.moneyflow.account.auth.dto.LoginRequest;
import com.moneyflow.account.auth.dto.LoginResponse;
import com.moneyflow.account.auth.dto.RegisterRequest;
import com.moneyflow.account.auth.entity.User;
import com.moneyflow.account.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ===== 註冊 =====
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) throws Exception {
        return authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getRole()
        );
    }

    // ===== 登入 =====
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) throws Exception {
    	System.out.println("token1");
        String token = authService.login(
                request.getUsername(),
                request.getPassword()
              
        );
        System.out.println("token: "+token);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return response;
    }
}