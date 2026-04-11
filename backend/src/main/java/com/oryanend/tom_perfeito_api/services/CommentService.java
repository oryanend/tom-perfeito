package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.dto.CommentDTO;
import com.oryanend.tom_perfeito_api.dto.CommentMinDTO;
import com.oryanend.tom_perfeito_api.entities.Comment;
import com.oryanend.tom_perfeito_api.entities.Music;
import com.oryanend.tom_perfeito_api.entities.User;
import com.oryanend.tom_perfeito_api.repositories.CommentRepository;
import com.oryanend.tom_perfeito_api.repositories.MusicRepository;
import com.oryanend.tom_perfeito_api.repositories.UserRepository;
import com.oryanend.tom_perfeito_api.services.exceptions.DatabaseException;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
  @Autowired private UserService userService;
  @Autowired private AuthService authService;

  @Autowired private MusicRepository musicRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CommentRepository repository;

  @Transactional(readOnly = true)
  public Page<CommentDTO> findByMusic(UUID musicId, Pageable pageable) {

    Music music =
        musicRepository
            .findById(musicId)
            .orElseThrow(() -> new ResourceNotFoundException("Music not found"));

    Page<Comment> list = repository.findByMusic(music, pageable);

    return list.map(CommentDTO::new);
  }

  @Transactional(readOnly = true)
  public CommentDTO findByIdAndMusic(Long commentId, UUID musicId) {
    Music music =
        musicRepository
            .findById(musicId)
            .orElseThrow(() -> new ResourceNotFoundException("Music not found"));

    Comment comment =
        repository
            .findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

    if (!comment.getMusic().getId().equals(music.getId())) {
      throw new ResourceNotFoundException("Comment does not belong to this music");
    }

    return new CommentDTO(comment);
  }

  @Transactional(readOnly = true)
  public Page<CommentMinDTO> findByUser(UUID userId, Pageable pageable) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Page<Comment> list = repository.findByAuthorId(user.getId(), pageable);

    return list.map(CommentMinDTO::new);
  }

  @Transactional
  public CommentDTO insert(UUID musicId, CommentDTO dto) {
    Music music =
        musicRepository
            .findById(musicId)
            .orElseThrow(() -> new ResourceNotFoundException("Music not found"));

    User user = userService.authenticated();

    Comment entity = new Comment();
    entity.setBody(dto.getBody());
    entity.setMusic(music);
    entity.setAuthor(user);

    if (dto.getParentId() != null) {
      Comment parentComment =
          repository
              .findById(dto.getParentId())
              .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

      entity.setParent(parentComment);
      parentComment.addReply(entity);
    }

    entity = repository.save(entity);
    return new CommentDTO(entity);
  }

  @Transactional
  public CommentDTO update(Long id, CommentDTO dto) {
    try {
      Comment entity = repository.getReferenceById(id);
      authService.validateCreatedCommentBySelfOrAdmin(entity);
      copyPatchDtoToEntity(dto, entity);
      entity = repository.save(entity);
      return new CommentDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Comment not found");
    }
  }

  @Transactional
  public void delete(Long id, UUID musicId) {
    try {
      Comment comment =
          repository
              .findById(id)
              .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

      if (!comment.getMusic().getId().equals(musicId)) {
        throw new ResourceNotFoundException("Comment does not belong to this music");
      }

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
