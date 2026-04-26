package com.oryanend.tom_perfeito_api.controllers;

import com.oryanend.tom_perfeito_api.dto.CommentDTO;
import com.oryanend.tom_perfeito_api.dto.CommentLikeResponseDTO;
import com.oryanend.tom_perfeito_api.services.CommentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/musics/{musicId}/comments")
public class CommentController {

  @Autowired private CommentService service;

  @GetMapping
  public ResponseEntity<Page<CommentDTO>> findAll(Pageable pageable, @PathVariable UUID musicId) {
    Page<CommentDTO> list = service.findByMusic(musicId, pageable);
    return ResponseEntity.ok(list);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<CommentDTO> findById(@PathVariable UUID musicId, @PathVariable Long id) {
    CommentDTO dto = service.findByIdAndMusic(id, musicId);
    return ResponseEntity.ok(dto);
  }

  @PreAuthorize("hasAnyRole('ROLE_CLIENT','ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<CommentDTO> insert(
      @PathVariable UUID musicId, @Valid @RequestBody CommentDTO dto) {

    CommentDTO result = service.insert(musicId, dto);
    URI uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(dto.getId())
            .toUri();

    return ResponseEntity.created(uri).body(result);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable UUID musicId) {
    service.delete(id, musicId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ROLE_CLIENT')")
  @PatchMapping(value = "/{id}")
  public ResponseEntity<CommentDTO> update(
      @PathVariable Long id, @Valid @RequestBody CommentDTO dto, @PathVariable UUID musicId) {
    dto = service.update(id, dto, musicId);
    return ResponseEntity.ok().body(dto);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENT')")
  @PostMapping("/{commentId}/like")
  public ResponseEntity<CommentLikeResponseDTO> addLike(
      @PathVariable Long commentId, @PathVariable UUID musicId) {
    CommentLikeResponseDTO commentLikeResponseDTO = service.addLike(commentId, musicId);
    return ResponseEntity.ok().body(commentLikeResponseDTO);
  }
}
