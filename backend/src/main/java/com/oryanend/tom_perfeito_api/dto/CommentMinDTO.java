package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Comment;
import java.time.Instant;
import java.util.UUID;

public class CommentMinDTO {

  private Long id;
  private String body;
  private Long likes;
  private Instant createdAt;

  private UserMinDTO author;
  private UUID musicId;

  public CommentMinDTO() {}

  public CommentMinDTO(
      Long id, String body, Long likes, Instant createdAt, UserMinDTO author, UUID musicId) {
    this.id = id;
    this.body = body;
    this.likes = likes;
    this.author = author;
    this.musicId = musicId;

    if (createdAt != null) {
      this.createdAt = createdAt;
    } else {
      this.createdAt = Instant.now();
    }
  }

  public CommentMinDTO(Comment entity) {
    this.id = entity.getId();
    this.body = entity.getBody();
    this.likes = entity.getLikes();
    this.author = new UserMinDTO(entity.getAuthor());
    this.musicId = entity.getMusic().getId();

    if (entity.getCreatedAt() != null) {
      this.createdAt = entity.getCreatedAt();
    } else {
      this.createdAt = Instant.now();
    }
  }

  public CommentMinDTO(CommentDTO entity) {
    this.id = entity.getId();
    this.body = entity.getBody();
    this.likes = entity.getLikes();
    this.author = entity.getAuthor();
    this.musicId = entity.getMusic().getId();

    if (entity.getCreatedAt() != null) {
      this.createdAt = entity.getCreatedAt();
    } else {
      this.createdAt = Instant.now();
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Long getLikes() {
    return likes;
  }

  public void setLikes(Long likes) {
    this.likes = likes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public UserMinDTO getAuthor() {
    return author;
  }

  public void setAuthor(UserMinDTO author) {
    this.author = author;
  }

  public UUID getMusicId() {
    return musicId;
  }

  public void setMusicId(UUID musicId) {
    this.musicId = musicId;
  }
}
