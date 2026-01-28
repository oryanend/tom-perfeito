package com.oryanend.tom_perfeito_api.dto;

import com.oryanend.tom_perfeito_api.entities.Lyric;
import com.oryanend.tom_perfeito_api.entities.LyricChord;

import java.util.ArrayList;
import java.util.List;

public class LyricDTO {
    private String text;
    private List<LyricChordDTO> chords = new ArrayList<>();

    public LyricDTO() {
    }

    public LyricDTO(String text, List<LyricChordDTO> chords) {
        this.text = text;
        this.chords = chords;
    }

    public LyricDTO(Lyric entity) {
        this.text = entity.getText();

        if (entity.getChords() != null){
            for (LyricChord lc : entity.getChords()) {
                this.chords.add(new LyricChordDTO(lc.getChord().getId(), lc.getPosition()));
            }
        }
    }

    public String getText() {
        return text;
    }

    public List<LyricChordDTO> getChords() {
        return chords;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setChords(List<LyricChordDTO> chords) {
        this.chords = chords;
    }

    public void addChord(Long chordId, Integer position) {
        this.chords.add(new LyricChordDTO(chordId, position));
    }
}
