package com.oryanend.tom_perfeito_api.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_lyric_chord")
public class LyricChord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lyric_id", nullable = false)
    private Lyric lyric;

    @ManyToOne
    @JoinColumn(name = "chord_id", nullable = false)
    private Chord chord;

    @Column(nullable = false)
    private Integer position;

    public LyricChord() {
    }

    public LyricChord(Lyric lyric, Chord chord, Integer position) {
        this.lyric = lyric;
        this.chord = chord;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lyric getLyric() {
        return lyric;
    }

    public void setLyric(Lyric lyric) {
        this.lyric = lyric;
    }

    public Chord getChord() {
        return chord;
    }

    public void setChord(Chord chord) {
        this.chord = chord;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LyricChord that = (LyricChord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
