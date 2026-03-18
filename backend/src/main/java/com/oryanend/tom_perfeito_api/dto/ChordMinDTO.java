package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Chord;

public class ChordMinDTO {
  private Long id;
  private String name;

  public ChordMinDTO() {}

  public ChordMinDTO(String name, Long id) {
    this.name = name;
    this.id = id;
  }

  public ChordMinDTO(Chord entity) {
    this.id = entity.getId();
    this.name = entity.getName();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
