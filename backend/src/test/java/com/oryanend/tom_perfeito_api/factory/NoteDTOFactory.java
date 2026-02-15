package com.oryanend.tom_perfeito_api.factory;

import com.oryanend.tom_perfeito_api.dto.NoteDTO;
import com.oryanend.tom_perfeito_api.entities.enums.Accidental;
import com.oryanend.tom_perfeito_api.entities.enums.NoteName;

public class NoteDTOFactory {
  public static NoteDTO createValidNoteDTO() {
    NoteDTO noteDTO = new NoteDTO();
    noteDTO.setName(NoteName.B);
    noteDTO.setAccidental(Accidental.NATURAL);
    return noteDTO;
  }

  public static NoteDTO createNullNameNoteDTO() {
    NoteDTO noteDTO = new NoteDTO();
    noteDTO.setAccidental(Accidental.NATURAL);
    noteDTO.setName(null);
    return noteDTO;
  }

  public static NoteDTO createNullAccidentalDTO() {
    NoteDTO noteDTO = new NoteDTO();
    noteDTO.setName(NoteName.B);
    noteDTO.setAccidental(null);
    return noteDTO;
  }
}
