package com.oryanend.tom_perfeito_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oryanend.tom_perfeito_api.entities.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.*;

@JsonPropertyOrder({
        "id", "username", "roles", "email", "createdAt", "updatedAt"
})
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(String username, String email, String password, Instant createdAt, Instant updatedAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        if (createdAt != null) {
            this.createdAt = createdAt;
        } else {
            this.createdAt = Instant.now();
        }
        if (updatedAt != null) {
            this.updatedAt = updatedAt;
        } else {
            this.updatedAt = Instant.now();
        }

    }

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.username = entity.getUsernameUser();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        if (entity.getCreatedAt() != null) {
            this.createdAt = entity.getCreatedAt();
        } else {
            this.createdAt = Instant.now();
        }
        if (entity.getUpdatedAt() != null) {
            this.updatedAt = entity.getUpdatedAt();
        } else {
            this.updatedAt = Instant.now();
        }
        entity.getRoles().forEach(role -> roles.add(new RoleDTO(role)));
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }
}
