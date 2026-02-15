package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.dto.NoteDTO;
import com.oryanend.tom_perfeito_api.entities.Chord;
import com.oryanend.tom_perfeito_api.entities.Note;
import com.oryanend.tom_perfeito_api.repositories.ChordRepository;
import com.oryanend.tom_perfeito_api.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {
    @Autowired
    private NoteRepository repository;

    @Autowired
    private ChordRepository chordRepository;

    @Transactional(readOnly = true)
    public List<NoteDTO> findAll(){
        List<Note> list = repository.findAll();
        return list.stream().map(NoteDTO::new).toList();
    }

    @Transactional
    public NoteDTO insert(NoteDTO dto) {
        Note entity = new Note();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new NoteDTO(entity);
    }

    private void copyDtoToEntity(NoteDTO dto, Note entity) {
        entity.setName(dto.getName());
        entity.setAccidental(dto.getAccidental());


        entity.getChords().clear();
        for (Chord chordDTO : dto.getChords()) {
            Chord chord = chordRepository.getReferenceById(chordDTO.getId());
            entity.getChords().add(chord);
        }
    }
}
