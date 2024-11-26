package com.iwa.messaging_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticatedUser implements Authentication {

    private boolean authenticated = true;

    private Long id;

    public JwtAuthenticatedUser(Long id) {
        this.id = id;
    }

    // Implémentez les méthodes de l'interface Authentication
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // Pas de rôles spécifiques
    }

    @Override
    public Object getCredentials() {
        return null;
    }


    @Override
    public Object getDetails() {
        return id;
    }

    @Override
    public Object getPrincipal() {
        return id;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return id.toString();
    }
}