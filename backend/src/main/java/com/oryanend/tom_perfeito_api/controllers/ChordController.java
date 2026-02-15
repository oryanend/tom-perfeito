package com.oryanend.tom_perfeito_api.controllers;

import com.oryanend.tom_perfeito_api.dto.ChordDTO;
import com.oryanend.tom_perfeito_api.services.ChordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/chords")
public class ChordController {
    @Autowired
    private ChordService service;

    @GetMapping
    public ResponseEntity<Page<ChordDTO>> findAll(Pageable pageable){
        Page<ChordDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChordDTO>> searchChords(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "notes", required = false) List<String> notes
    ) {
        List<ChordDTO> list = service.searchChords(name, notes);
        return ResponseEntity.ok(list);
    }


    @PostMapping
    public ResponseEntity<ChordDTO> insert(@Valid @RequestBody ChordDTO dto){
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
