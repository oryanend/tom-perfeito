package com.oryanend.tom_perfeito_api.dto;

public class LyricChordDTO {

  private Long chordId;
  private Integer position;
  private String name;

  public LyricChordDTO() {}

  public LyricChordDTO(Long chordId, Integer position, String name) {
    this.chordId = chordId;
    this.position = position;
    this.name = name;
  }

  public Long getChordId() {
    return chordId;
  }

  public Integer getPosition() {
    return position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
