package com.oryanend.tom_perfeito_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oryanend.tom_perfeito_api.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonPropertyOrder({"id", "username", "roles", "email", "createdAt", "updatedAt"
})
public class UserDTO {
    private UUID id;

    @NotNull(message = "Username cannot be null")
    @Size(message = "Username must be between 3 and 40 characters", min = 3, max = 40)
    private String username;

    @Email(message = "Email should be valid")
    @Size(message = "Email must be between 5 and 254 characters", min = 5, max = 254)
    @NotNull(message = "Email cannot be null")
    private String email;

    @Size(message = "Password must have 5 characters at least", min = 5)
    @NotNull(message = "Password cannot be null")
    private String password;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO() {
    }

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

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
