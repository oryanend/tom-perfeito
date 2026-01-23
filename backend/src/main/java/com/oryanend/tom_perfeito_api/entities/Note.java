package com.oryanend.tom_perfeito_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oryanend.tom_perfeito_api.entities.enums.Accidental;
import com.oryanend.tom_perfeito_api.entities.enums.NoteName;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NoteName name;

    @Enumerated(EnumType.STRING)
    private Accidental accidental;

    @ManyToMany(mappedBy = "notes", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Chord> chords = new HashSet<>();

    public Note() {
    }

    public Note(NoteName name, Accidental accidental) {
        this.name = name;
        this.accidental = accidental;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NoteName getName() {
        return name;
    }

    public void setName(NoteName name) {
        this.name = name;
    }

    public Set<Chord> getChords() {
        return chords;
    }

    public Set<Chord> addNote(Chord chord) {
        this.chords.add(chord);
        return this.chords;
    }

    public Accidental getAccidental() {
        return accidental;
    }

    public void setAccidental(Accidental accidental) {
        this.accidental = accidental;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
