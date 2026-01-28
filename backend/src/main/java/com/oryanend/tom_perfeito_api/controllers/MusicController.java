package com.oryanend.tom_perfeito_api.controllers;

import com.oryanend.tom_perfeito_api.dto.MusicDTO;
import com.oryanend.tom_perfeito_api.dto.MusicMinDTO;
import com.oryanend.tom_perfeito_api.dto.MusicPatchDTO;
import com.oryanend.tom_perfeito_api.services.MusicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/musics")
public class MusicController {
    @Autowired
    private MusicService service;

    @GetMapping
    public ResponseEntity<Page<MusicMinDTO>> findAll(Pageable pageable, @RequestParam(value = "name", required = false) String name){
        Page<MusicMinDTO> list = service.findAllPaged(name,pageable);
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MusicDTO> findById(@PathVariable String id){
        MusicDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping
    public ResponseEntity<MusicDTO> insert(@Valid @RequestBody MusicDTO dto){
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<MusicPatchDTO> update(@PathVariable UUID id,@Valid @RequestBody MusicPatchDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENT')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
