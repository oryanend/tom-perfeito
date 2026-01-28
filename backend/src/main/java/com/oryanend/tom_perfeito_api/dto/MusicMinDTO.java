package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.controllers.MusicController;
import com.oryanend.tom_perfeito_api.entities.Music;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MusicMinDTO {

    private UUID id;
    private String title;
    private String description;
    private String link;

    public MusicMinDTO() {
    }

    public MusicMinDTO(String title, String description) {
        this.description = description;
        this.title = title;
    }

    public MusicMinDTO(Music entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.link = linkTo(methodOn(MusicController.class).findById(entity.getId().toString())).toUri().toString();
    }

    public MusicMinDTO(MusicDTO entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.link = linkTo(methodOn(MusicController.class).findById(entity.getId().toString())).toUri().toString();
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public UUID getId() {
        return id;
    }
}
