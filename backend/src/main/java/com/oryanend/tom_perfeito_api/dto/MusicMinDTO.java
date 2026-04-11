package com.oryanend.tom_perfeito_api.dto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.oryanend.tom_perfeito_api.controllers.MusicController;
import com.oryanend.tom_perfeito_api.entities.Music;
import com.oryanend.tom_perfeito_api.projections.MusicProjection;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class MusicMinDTO implements Serializable {

  private UUID id;
  private String title;
  private String description;
  private String link;

  private Instant createdAt;
  private UserMinDTO createdBy;

  public MusicMinDTO() {}

  public MusicMinDTO(Music entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.description = entity.getDescription();
    this.createdAt = entity.getCreatedAt();
    this.createdBy = new UserMinDTO(entity.getCreatedBy());
    this.link =
        linkTo(methodOn(MusicController.class).findById(entity.getId().toString()))
            .toUri()
            .toString();
  }

  public MusicMinDTO(MusicProjection entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.description = entity.getDescription();
    this.createdAt = entity.getCreatedAt();
    this.createdBy = new UserMinDTO(entity.getCreatedBy());
    this.link =
        linkTo(methodOn(MusicController.class).findById(entity.getId().toString()))
            .toUri()
            .toString();
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public UserMinDTO getCreatedBy() {
    return createdBy;
  }
}
