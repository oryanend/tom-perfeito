package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Chord;
import com.oryanend.tom_perfeito_api.entities.Note;
import com.oryanend.tom_perfeito_api.entities.enums.Accidental;
import com.oryanend.tom_perfeito_api.entities.enums.NoteName;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

public class NoteDTO {
  private Long id;

  @NotNull(message = "Name cannot be null")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 1)
  private NoteName name;

  @NotNull(message = "Accidental cannot be null")
  @Enumerated(EnumType.STRING)
  private Accidental accidental;

  private Set<Chord> chords = new HashSet<>();

  public NoteDTO() {}

  public NoteDTO(NoteName name, Accidental accidental) {
    this.name = name;
    this.accidental = accidental;
  }

  public NoteDTO(Note entity) {
    this.id = entity.getId();
    this.name = entity.getName();
    this.accidental = entity.getAccidental();

    for (Chord chord : entity.getChords()) {
      addChords(chord);
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public NoteName getName() {
    return name;
  }

  public void setName(NoteName name) {
    this.name = name;
  }

  public Set<Chord> getChords() {
    return chords;
  }

  public void addChords(Chord chord) {
    this.chords.add(chord);
  }

  public Accidental getAccidental() {
    return accidental;
  }

  public void setAccidental(Accidental accidental) {
    this.accidental = accidental;
  }
}
