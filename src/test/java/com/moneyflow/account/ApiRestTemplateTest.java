package com.moneyflow.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

    @BeforeEach
    public void login() {
        var loginRequest = Map.of(
            "username", "mike",
            "password", "123456"
        );//你這邊要確認資料庫真的有這個人，還有密碼也是正確的

        ResponseEntity<Map> response = restTemplate.postForEntity("/auth/login", loginRequest, Map.class);
        jwtToken = (String) response.getBody().get("token");
    }

    @Test
    public void testHelloApi() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/hello", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello Swagger + Spring Boot 17!", response.getBody());
    }
}
