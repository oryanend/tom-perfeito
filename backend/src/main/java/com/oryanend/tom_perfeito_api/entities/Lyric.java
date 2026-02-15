package com.oryanend.tom_perfeito_api.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_lyric")
public class Lyric {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  private String text;

  @OneToMany(mappedBy = "lyric", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LyricChord> chords = new ArrayList<>();

  @OneToOne
  @JoinColumn(name = "music_id", referencedColumnName = "id")
  private Music music;

  public Lyric() {}

  public Lyric(UUID id, String text) {
    this.id = id;
    this.text = text;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<LyricChord> getChords() {
    return chords;
  }

  public void addChord(Chord chord, Integer position) {
    LyricChord lc = new LyricChord(this, chord, position);
    chords.add(lc);
  }

  public Music getMusic() {
    return music;
  }

  public void setMusic(Music music) {
    this.music = music;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Lyric lyric = (Lyric) o;
    return Objects.equals(id, lyric.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
