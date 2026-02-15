package com.oryanend.tom_perfeito_api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tb_music")
public class Music {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 40)
  private String title;

  @Column(nullable = false, unique = true, length = 254)
  private String description;

  @Column(name = "release_date")
  private LocalDate releaseDate;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "created_at")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false, name = "updated_at")
  private Instant updatedAt;

  @OneToOne(mappedBy = "music", cascade = CascadeType.ALL)
  private Lyric lyric;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonBackReference
  private User createdBy;

  @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  public Music() {}

  public Music(
      UUID id,
      String title,
      String description,
      LocalDate releaseDate,
      Lyric lyric,
      User createdBy) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.releaseDate = releaseDate;
    this.lyric = lyric;
    this.createdBy = createdBy;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Lyric getLyric() {
    return lyric;
  }

  public void setLyric(Lyric lyric) {
    this.lyric = lyric;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Music music = (Music) o;
    return Objects.equals(id, music.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
