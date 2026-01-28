package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Music;

import java.time.Instant;
import java.time.LocalDate;

public class MusicPatchDTO {
    private String title;
    private String description;
    private LocalDate releaseDate;
    private Instant updatedAt;
    private LyricDTO lyric;

    public MusicPatchDTO() {
    }

    public MusicPatchDTO(String title, String description, LocalDate releaseDate, LyricDTO lyric, Instant updatedAt) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.lyric = lyric;
        this.updatedAt = Instant.now();
    }

    public MusicPatchDTO(Music entity) {
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.releaseDate = entity.getReleaseDate();
        this.lyric = new LyricDTO(entity.getLyric());
        this.updatedAt = Instant.now();
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LyricDTO getLyric() {
        return lyric;
    }

    public void setLyric(LyricDTO lyric) {
        this.lyric = lyric;
    }
}
