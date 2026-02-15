package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Music;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MusicDTO {
  private UUID id;

  @Column(nullable = false, unique = true, length = 40)
  @NotNull(message = "Title cannot be null")
  private String title;

  @Column(nullable = false, unique = true, length = 254)
  @NotNull(message = "Description cannot be null")
  private String description;

  @NotNull(message = "ReleaseDate cannot be null")
  private LocalDate releaseDate;

  private Instant createdAt;
  private Instant updatedAt;

  @NotNull(message = "Lyric cannot be null")
  private LyricDTO lyric;

  private UserMinDTO createdBy;

  private Set<CommentDTO> comments = new HashSet<>();

  public MusicDTO() {}

  public MusicDTO(Music entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.description = entity.getDescription();
    this.releaseDate = entity.getReleaseDate();
    this.lyric = new LyricDTO(entity.getLyric());
    this.createdBy = new UserMinDTO(entity.getCreatedBy());

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
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LyricDTO getLyric() {
    return lyric;
  }

  public void setLyric(LyricDTO lyric) {
    this.lyric = lyric;
  }

  public UserMinDTO getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(UserMinDTO createdBy) {
    this.createdBy = createdBy;
  }

  public Set<CommentDTO> getComments() {
    return comments;
  }

  public void addComment(CommentDTO comment) {
    comments.add(comment);
  }
}
