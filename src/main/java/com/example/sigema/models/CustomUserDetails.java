package com.example.sigema.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.sigema.models.enums.Rol;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Rol rol;
    private final Long idUnidad;
    private final Long idUsuario;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, Rol rol, Long idUnidad, Long idUsuario, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.idUnidad = idUnidad;
        this.idUsuario = idUsuario;
        this.authorities = authorities;
    }

    public Rol getRol() {
        return rol;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdUnidad() {
        return idUnidad;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}