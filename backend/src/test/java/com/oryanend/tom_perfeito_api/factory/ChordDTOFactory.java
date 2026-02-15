package com.oryanend.tom_perfeito_api.factory;

import com.oryanend.tom_perfeito_api.dto.ChordDTO;
import com.oryanend.tom_perfeito_api.entities.enums.ChordType;
import com.oryanend.tom_perfeito_api.entities.enums.NoteName;
import com.oryanend.tom_perfeito_api.repositories.NoteRepository;

public class ChordDTOFactory {
  public static ChordDTO createChordDTO(
      NoteRepository repository, NoteName name, ChordType type, Long... noteIds) {
    ChordDTO chordDTO = new ChordDTO();
    chordDTO.setName(name.name());
    chordDTO.setType(type);

    for (Long noteId : noteIds) {
      chordDTO.getNotes().add(repository.findById(noteId).orElseThrow());
    }

    return chordDTO;
  }

  public static ChordDTO createValidChordDTO(NoteRepository repository) {
    ChordDTO validChordDTO = new ChordDTO();
    validChordDTO.setName("A Minor");
    validChordDTO.setType(ChordType.MINOR);

    validChordDTO.getNotes().add(repository.findById(1L).orElseThrow());
    validChordDTO.getNotes().add(repository.findById(5L).orElseThrow());
    validChordDTO.getNotes().add(repository.findById(10L).orElseThrow());

    return validChordDTO;
  }

  public static ChordDTO createWithoutNameChordDTO(NoteRepository repository) {
    ChordDTO chordDTO = new ChordDTO();
    chordDTO.setName(null); // Null name
    chordDTO.setType(ChordType.MINOR);

    chordDTO.getNotes().add(repository.findById(1L).orElseThrow());
    chordDTO.getNotes().add(repository.findById(5L).orElseThrow());
    chordDTO.getNotes().add(repository.findById(10L).orElseThrow());

    return chordDTO;
  }

  public static ChordDTO createWithoutTypeChordDTO(NoteRepository repository) {
    ChordDTO chordDTO = new ChordDTO();
    chordDTO.setName("A Minor");
    chordDTO.setType(null); // Null type

    chordDTO.getNotes().add(repository.findById(1L).orElseThrow());
    chordDTO.getNotes().add(repository.findById(5L).orElseThrow());
    chordDTO.getNotes().add(repository.findById(10L).orElseThrow());

    return chordDTO;
  }

  public static ChordDTO createWithoutNotesChordDTO() {
    ChordDTO chordDTO = new ChordDTO();
    chordDTO.setName("A Minor");
    chordDTO.setType(ChordType.MINOR);
    return chordDTO;
  }

  public static ChordDTO createInvalidChordDTO() {
    ChordDTO chordDTO = new ChordDTO();
    chordDTO.setName("NonExistingChordName"); // Invalid name
    chordDTO.setType(null); // Invalid type
    return chordDTO;
  }
}
