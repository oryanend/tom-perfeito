package com.oryanend.tom_perfeito_api.dto;

public class CommentLikeDTO {
  private Long id;
  private UserMinDTO userMinDTO;
  private CommentMinDTO commentMinDTO;

  public CommentLikeDTO() {}

  public CommentLikeDTO(UserMinDTO userMinDTO, CommentMinDTO commentMinDTO) {
    this.userMinDTO = userMinDTO;
    this.commentMinDTO = commentMinDTO;
  }

  public Long getId() {
    return id;
  }

  public UserMinDTO getUserMinDTO() {
    return userMinDTO;
  }

  public void setUserMinDTO(UserMinDTO userMinDTO) {
    this.userMinDTO = userMinDTO;
  }

  public CommentMinDTO getCommentMinDTO() {
    return commentMinDTO;
  }

  public void setCommentMinDTO(CommentMinDTO commentMinDTO) {
    this.commentMinDTO = commentMinDTO;
  }
}
