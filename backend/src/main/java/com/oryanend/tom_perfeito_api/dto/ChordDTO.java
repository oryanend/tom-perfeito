package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Chord;
import com.oryanend.tom_perfeito_api.entities.Note;
import com.oryanend.tom_perfeito_api.entities.enums.ChordType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class ChordDTO {

    private Long id;

    @NotNull(message = "Chord name cannot be null")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Chord type cannot be null")
    private ChordType type;

    @Size(message = "A chord must have at least three note", min = 3)
    @NotNull(message = "Notes cannot be null")
    private Set<Note> notes = new HashSet<>();

    public ChordDTO() {
    }

    public ChordDTO(String name, ChordType type) {
        this.name = name;
        this.type = type;
    }

    public ChordDTO(Chord entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.type = entity.getType();

        this.notes.addAll(entity.getNotes());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChordType getType() {
        return type;
    }

    public void setType(ChordType type) {
        this.type = type;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }
}
