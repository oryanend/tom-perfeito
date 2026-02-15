package com.oryanend.tom_perfeito_api.controllers;

import com.oryanend.tom_perfeito_api.dto.NoteDTO;
import com.oryanend.tom_perfeito_api.services.NoteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/notes")
public class NoteController {
  @Autowired private NoteService service;

  @GetMapping
  public ResponseEntity<List<NoteDTO>> findAll() {
    List<NoteDTO> list = service.findAll();
    return ResponseEntity.ok(list);
  }

  @PostMapping
  public ResponseEntity<NoteDTO> insert(@Valid @RequestBody NoteDTO dto) {
    dto = service.insert(dto);
    URI uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(dto.getId())
            .toUri();
    return ResponseEntity.created(uri).body(dto);
  }
}
