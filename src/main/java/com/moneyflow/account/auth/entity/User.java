package com.moneyflow.account.auth.entity;

import jakarta.persistence.*;


/**
 * 
 */
@Entity
@Table(name = "TB_USERS")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // bcrypt 加密後存

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private String role; // 例如 "USER" 或 "ADMIN"

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public User(Long id, String username, String password, String email, Boolean isActive, String role) {
		
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.isActive = isActive;
		this.role = role;
	}

	public User() {
		
	}
    
    
}