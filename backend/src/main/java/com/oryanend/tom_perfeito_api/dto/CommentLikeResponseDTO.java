package com.oryanend.tom_perfeito_api.dto;

public class CommentLikeResponseDTO {
  private Long commentId;
  private Long likes;
  private Boolean likedByUser;

  public CommentLikeResponseDTO() {}

  public CommentLikeResponseDTO(Long commentId, Long likes, boolean likedByUser) {
    this.commentId = commentId;
    this.likes = likes;
    this.likedByUser = likedByUser;
  }

  public Long getCommentId() {
    return commentId;
  }

  public Long getLikes() {
    return likes;
  }

  public Boolean getLikedByUser() {
    return likedByUser;
  }
}
