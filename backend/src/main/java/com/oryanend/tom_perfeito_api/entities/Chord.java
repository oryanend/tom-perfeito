package com.oryanend.tom_perfeito_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oryanend.tom_perfeito_api.entities.enums.ChordType;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "tb_chord")
public class Chord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ChordType type;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "tb_chord_note",
            joinColumns = @JoinColumn(name = "chord_id"),
            inverseJoinColumns = @JoinColumn(name = "note_id")
    )
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "chord")
    private List<LyricChord> lyric = new ArrayList<>();

    public Chord() {
    }

    public Chord(String name, ChordType type) {
        this.name = name;
        this.type = type;
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

    public Set<Note> addNote(Note note) {
        this.notes.add(note);
        return this.notes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Chord chord = (Chord) o;
        return Objects.equals(id, chord.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
