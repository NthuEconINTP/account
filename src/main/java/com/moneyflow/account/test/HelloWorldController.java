package com.moneyflow.account.test;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import com.moneyflow.account.auth.security.SecurityUtil;
// 想要快速測試的
@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    @Operation(summary = "Say hello")
    public String hello() {
    	
        return "Hello Swagger + Spring Boot 17! userid: "+SecurityUtil.getCurrentUserId()+" user name: " +SecurityUtil.getCurrentUser().getUsername();
    }
    
    
}