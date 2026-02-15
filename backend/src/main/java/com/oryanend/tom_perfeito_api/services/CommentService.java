package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.dto.CommentDTO;
import com.oryanend.tom_perfeito_api.entities.Comment;
import com.oryanend.tom_perfeito_api.entities.Music;
import com.oryanend.tom_perfeito_api.entities.User;
import com.oryanend.tom_perfeito_api.repositories.CommentRepository;
import com.oryanend.tom_perfeito_api.repositories.MusicRepository;
import com.oryanend.tom_perfeito_api.services.exceptions.DatabaseException;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    @Autowired private MusicRepository musicRepository;
    @Autowired private CommentRepository repository;

    @Transactional(readOnly = true)
    public Page<CommentDTO> findAllPaged(Pageable pageable){
        Page<Comment> list = repository.findAll(pageable);
        return list.map(CommentDTO::new);
    }

    @Transactional(readOnly = true)
    public CommentDTO findById(Long id) {
        Comment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return new CommentDTO(entity);
    }

    @Transactional
    public CommentDTO insert(UUID musicId, CommentDTO dto) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new ResourceNotFoundException("Music not found"));

        User user = userService.authenticated();

        Comment entity = new Comment();
        entity.setBody(dto.getBody());
        entity.setMusic(music);
        entity.setAuthor(user);

        if (dto.getParentId() != null) {
            Comment parentComment = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            entity.setParent(parentComment);
            parentComment.addReply(entity);
        }

        entity = repository.save(entity);
        return new CommentDTO(entity);
    }

    @Transactional
    public CommentDTO update(Long id, CommentDTO dto) {
        try{
            Comment entity = repository.getReferenceById(id);
            authService.validateCreatedCommentBySelfOrAdmin(entity);
            copyPatchDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new CommentDTO(entity);
        }
        catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Comment not found");
        }
    }
    @Transactional
    public void delete(Long id) {
        try{
            Comment comment = repository.getReferenceById(id);
            authService.validateCreatedCommentBySelfOrAdmin(comment);

            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity error");
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Comment not found");
        }
    }

    private void copyPatchDtoToEntity(CommentDTO dto, Comment entity) {
        if (dto.getBody() != null) {
            entity.setBody(dto.getBody());
        }
        entity.setUpdatedAt(Instant.now());
    }
}
