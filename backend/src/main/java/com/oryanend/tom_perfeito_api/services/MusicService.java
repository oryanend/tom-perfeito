package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.dto.LyricChordDTO;
import com.oryanend.tom_perfeito_api.dto.MusicDTO;
import com.oryanend.tom_perfeito_api.dto.MusicMinDTO;
import com.oryanend.tom_perfeito_api.dto.MusicPatchDTO;
import com.oryanend.tom_perfeito_api.entities.Chord;
import com.oryanend.tom_perfeito_api.entities.Lyric;
import com.oryanend.tom_perfeito_api.entities.Music;
import com.oryanend.tom_perfeito_api.entities.User;
import com.oryanend.tom_perfeito_api.repositories.ChordRepository;
import com.oryanend.tom_perfeito_api.repositories.MusicRepository;
import com.oryanend.tom_perfeito_api.services.exceptions.DatabaseException;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MusicService {

    @Autowired private MusicRepository repository;
    @Autowired private ChordRepository chordRepository;
    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    @Transactional(readOnly = true)
    public Page<MusicMinDTO> findAllPaged(String name, Pageable pageable){
        if (name != null && !name.isEmpty()) {
            return findByNameContaining(name, pageable);
        }

        Page<Music> list = repository.findAll(pageable);
        return list.map(MusicMinDTO::new);
    }

    public Page<MusicMinDTO> findByNameContaining(String name, Pageable pageable) {
        Page<Music> list = repository.findByTitleContainingIgnoreCase(name, pageable);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No musics found with name containing: " + name);
        }
        return list.map(MusicMinDTO::new);
    }

    @Transactional(readOnly = true)
    public MusicDTO findById(String id){
        Music entity = repository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException("Music not found"));
        return new MusicDTO(entity);
    }

    @Transactional
    public MusicDTO insert(MusicDTO dto) {
        Music entity = new Music();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new MusicDTO(entity);
    }

    @Transactional
    public MusicPatchDTO update(UUID id, MusicPatchDTO dto) {
        try{
            Music entity = repository.getReferenceById(id);
            authService.validateCreatedBySelfOrAdmin(entity);
            copyPatchDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new MusicPatchDTO(entity);
        }
        catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Music not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(UUID id) {
        try{
            Music music = repository.getReferenceById(id);
            authService.validateCreatedBySelfOrAdmin(music);

            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity error");
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Music not found");
        }
    }

    private void copyDtoToEntity(MusicDTO dto, Music entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReleaseDate(dto.getReleaseDate());

        User createdBy = userService.authenticated();
        entity.setCreatedBy(createdBy);

        if (dto.getLyric() != null) {
            Lyric lyric = new Lyric();
            lyric.setText(dto.getLyric().getText());
            lyric.setMusic(entity);

            for (LyricChordDTO c : dto.getLyric().getChords()) {
                Chord chord = chordRepository.getReferenceById(c.getChordId());
                lyric.addChord(chord, c.getPosition());
            }

            entity.setLyric(lyric);
        }
    }

    private void copyPatchDtoToEntity(MusicPatchDTO dto, Music entity) {
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getReleaseDate() != null) {
            entity.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getLyric() != null) {
            Lyric lyric = entity.getLyric();
            if (lyric == null) {
                lyric = new Lyric();
                lyric.setMusic(entity);
                entity.setLyric(lyric);
            }
            if (dto.getLyric().getText() != null) {
                lyric.setText(dto.getLyric().getText());
            }
            if (dto.getLyric().getChords() != null) {
                lyric.getChords().clear();
                for (LyricChordDTO c : dto.getLyric().getChords()) {
                    Chord chord = chordRepository.getReferenceById(c.getChordId());
                    lyric.addChord(chord, c.getPosition());
                }
            }
        }
    }
}
