package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.dto.ChordDTO;
import com.oryanend.tom_perfeito_api.dto.ChordMinDTO;
import com.oryanend.tom_perfeito_api.entities.Chord;
import com.oryanend.tom_perfeito_api.entities.Note;
import com.oryanend.tom_perfeito_api.repositories.ChordRepository;
import com.oryanend.tom_perfeito_api.repositories.NoteRepository;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChordService {
  @Autowired private ChordRepository repository;
  @Autowired private NoteRepository noteRepository;

  @Transactional(readOnly = true)
  public Page<ChordDTO> findAllPaged(Pageable pageable) {
    Page<Chord> list = repository.findAll(pageable);
    return list.map(ChordDTO::new);
  }

  @Transactional(readOnly = true)
  @Cacheable("chords")
  public List<ChordMinDTO> searchChords(String name, List<String> notes) {

    if (notes != null && !notes.isEmpty()) {

      List<String> normalizedNotes =
          notes.stream()
              .filter(n -> n != null && !n.isBlank())
              .map(String::trim)
              .map(String::toUpperCase)
              .toList();

      return repository.findByNameAndNotes(name, normalizedNotes, normalizedNotes.size()).stream()
          .map(p -> new ChordMinDTO(p.getName(), p.getId()))
          .toList();
    }

    if (name != null && !name.isBlank()) {
      return repository.findByNameStartingWithIgnoreCase(name).stream()
          .map(ChordMinDTO::new)
          .toList();
    }

    return repository.findAll().stream().map(ChordMinDTO::new).toList();
  }

  @Transactional
  public ChordDTO insert(ChordDTO dto) {
    Chord entity = new Chord();
    copyDtoToEntity(dto, entity);
    entity = repository.save(entity);
    return new ChordDTO(entity);
  }

  private void copyDtoToEntity(ChordDTO dto, Chord entity) {
    entity.setName(dto.getName());
    entity.setType(dto.getType());
    entity.setId(dto.getId());

    entity.getNotes().clear();
    for (Note noteDTO : dto.getNotes()) {
      Note note =
          noteRepository
              .findById(noteDTO.getId())
              .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteDTO));
      entity.getNotes().add(note);
    }
  }
}
