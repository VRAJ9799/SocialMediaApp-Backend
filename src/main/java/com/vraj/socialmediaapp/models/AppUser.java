package com.vraj.socialmediaapp.models;

import com.vraj.socialmediaapp.models.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AppUser implements UserDetails {
    private final User _user;

    public AppUser(User _user) {
        this._user = _user;
    }

    public User getUser() {
        return _user;
    }

    public Long getId() {
        return _user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(_user.getRole().getName()));
        return roles;
    }

    @Override
    public String getPassword() {
        return _user.getPassword();
    }

    @Override
    public String getUsername() {
        return _user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (_user.isLockedOut() && Date.from(Instant.now()).getTime() > _user.getLockedOutExpireOn().getTime())
            return _user.isLockedOut();
        return !_user.isLockedOut();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !_user.isDeleted();
    }
}
