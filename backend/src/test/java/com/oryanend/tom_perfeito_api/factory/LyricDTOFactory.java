package com.oryanend.tom_perfeito_api.factory;

import com.oryanend.tom_perfeito_api.dto.LyricDTO;

public class LyricDTOFactory {
    public static LyricDTO createValidLyricDTO() {
        LyricDTO lyricDTO = new LyricDTO();
        lyricDTO.setText("Imagine all the people...");
        lyricDTO.addChord(1L, 0);
        lyricDTO.addChord(2L, 15);
        return lyricDTO;
    }
}
