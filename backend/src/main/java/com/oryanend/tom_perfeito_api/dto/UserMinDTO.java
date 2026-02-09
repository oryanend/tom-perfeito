package com.oryanend.tom_perfeito_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oryanend.tom_perfeito_api.controllers.UserController;
import com.oryanend.tom_perfeito_api.entities.User;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@JsonPropertyOrder({"id", "username", "email"})
public class UserMinDTO {
    private UUID id;
    private String username;
    private String email;
    private String link;

    public UserMinDTO() {
    }

    public UserMinDTO(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UserMinDTO(User entity) {
        this.id = entity.getId();
        this.username = entity.getUsernameUser();
        this.email = entity.getEmail();
        this.link = linkTo(methodOn(UserController.class).findById(entity.getId().toString())).toUri().toString();
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

    public String getLink() {
        return link;
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
