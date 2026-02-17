package com.moneyflow.account.auth.controller;

import com.moneyflow.account.auth.dto.LoginRequest;
import com.moneyflow.account.auth.dto.LoginResponse;
import com.moneyflow.account.auth.dto.RegisterRequest;
import com.moneyflow.account.auth.entity.User;
import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.auth.service.AuthService;
import com.moneyflow.account.common.ApiResponse;
import com.moneyflow.account.common.ApiResponseUtil;
import com.moneyflow.account.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;

    // ===== 註冊 =====
    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest dto) throws Exception  {
    	
    	User user = authService.register(dto.getUsername(), dto.getPassword(), dto.getEmail(), null); //這邊寫的不好 故意留著
    	return ApiResponseUtil.success("Register success", user);
     
    }

    // ===== 登入 =====
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
//    	System.out.println("token1");
        User user = authService.login(
                request.getUsername(),
                request.getPassword()
              
        );
        user.setPassword("");
//      System.out.println("token: "+token);
        LoginResponse response = new LoginResponse();
        String token=jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        response.setToken(token);
        response.setCurrentUser( user    );
        return ApiResponseUtil.success("Login success",  response);
    }
}