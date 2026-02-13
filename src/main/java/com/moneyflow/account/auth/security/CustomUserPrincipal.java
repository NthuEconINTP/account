package com.moneyflow.account.auth.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserPrincipal implements UserDetails {

	private static final long serialVersionUID = 1L;
	private Long userId;
    private String username;
    private String role;
    private boolean isActive;

    public CustomUserPrincipal(Long userId, String username, String role, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.isActive = isActive;
    }

    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public boolean getIsActive() { return isActive; }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return null; } // 不存密碼

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 可自己映射 role → GrantedAuthority ==>以後再補，目前傾向自己控制RBAC不交給security
    }

    @Override
    public boolean isAccountNonExpired() { return isActive; }

    @Override
    public boolean isAccountNonLocked() { return isActive; }

    @Override
    public boolean isCredentialsNonExpired() { return isActive; }

    @Override
    public boolean isEnabled() { return isActive; }
}
