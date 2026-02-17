package com.moneyflow.account.auth.dto;

import com.moneyflow.account.auth.entity.User;


public class LoginResponse {

    private String token;
    private User currentUser;
    

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

    
    
}
