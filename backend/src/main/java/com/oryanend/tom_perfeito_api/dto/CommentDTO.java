package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Comment;
import java.time.Instant;
import java.util.List;

public class CommentDTO {

  private Long id;
  private String body;
  private Long likes;
  private Instant createdAt;
  private Instant updatedAt;

  private Long parentId;
  private UserMinDTO author;
  private MusicMinDTO music;
  private List<CommentDTO> replies;

  public CommentDTO() {}

  public CommentDTO(
      Long id,
      String body,
      Long likes,
      Instant createdAt,
      Instant updatedAt,
      UserMinDTO author,
      MusicMinDTO music,
      Long parentId,
      List<CommentDTO> replies) {
    this.id = id;
    this.body = body;
    this.likes = likes;
    this.author = author;
    this.music = music;
    this.parentId = parentId;

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

    this.replies = replies;
  }

  public CommentDTO(Comment entity) {
    this.id = entity.getId();
    this.body = entity.getBody();
    this.likes = entity.getLikes();
    this.author = new UserMinDTO(entity.getAuthor());
    this.music = new MusicMinDTO(entity.getMusic());
    this.parentId = entity.getParent() != null ? entity.getParent().getId() : null;

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

    this.replies = entity.getReplies().stream().map(CommentDTO::new).toList();
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

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UserMinDTO getAuthor() {
    return author;
  }

  public void setAuthor(UserMinDTO author) {
    this.author = author;
  }

  public MusicMinDTO getMusic() {
    return music;
  }

  public void setMusic(MusicMinDTO music) {
    this.music = music;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public List<CommentDTO> getReplies() {
    return replies;
  }

  public void addReply(CommentDTO reply) {
    this.replies.add(reply);
  }
}
