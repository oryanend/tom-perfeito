package com.oryanend.tom_perfeito_api.dto;

public class LyricChordDTO {

  private Long chordId;
  private Integer position;

  public LyricChordDTO() {}

  public LyricChordDTO(Long chordId, Integer position) {
    this.chordId = chordId;
    this.position = position;
  }

  public Long getChordId() {
    return chordId;
  }

  public Integer getPosition() {
    return position;
  }
}
