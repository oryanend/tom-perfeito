package com.oryanend.tom_perfeito_api.controllers;

import com.oryanend.tom_perfeito_api.dto.CommentMinDTO;
import com.oryanend.tom_perfeito_api.services.CommentService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users/{userId}/comments")
public class UserCommentController {
  @Autowired private CommentService service;

  @GetMapping
  public ResponseEntity<Page<CommentMinDTO>> findAllByUserId(
      @PathVariable UUID userId, Pageable pageable) {
    Page<CommentMinDTO> list = service.findByUser(userId, pageable);
    return ResponseEntity.ok(list);
  }
}
