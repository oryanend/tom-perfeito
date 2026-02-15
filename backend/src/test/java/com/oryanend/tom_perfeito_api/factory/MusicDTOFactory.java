package com.oryanend.tom_perfeito_api.factory;

import static com.oryanend.tom_perfeito_api.factory.LyricDTOFactory.createValidLyricDTO;

import com.oryanend.tom_perfeito_api.dto.MusicDTO;
import com.oryanend.tom_perfeito_api.dto.MusicPatchDTO;
import java.time.LocalDate;

public class MusicDTOFactory {
  public static MusicPatchDTO createValidMusicPatchDTO() {
    MusicPatchDTO musicPatchDTO = new MusicPatchDTO();

    musicPatchDTO.setTitle("Musica Atualizada");
    musicPatchDTO.setDescription("Essa musica foi atualizada.");
    musicPatchDTO.setReleaseDate(LocalDate.of(2026, 1, 1));
    musicPatchDTO.setLyric(createValidLyricDTO());

    return musicPatchDTO;
  }

  public static MusicDTO createValidMusicDTO() {
    MusicDTO musicDTO = new MusicDTO();

    musicDTO.setTitle("Imagine");
    musicDTO.setDescription("Uma música icônica de John Lennon.");
    musicDTO.setReleaseDate(LocalDate.of(1971, 10, 11));
    musicDTO.setLyric(createValidLyricDTO()); // Assuming lyric can be null for this factory method

    return musicDTO;
  }

  public static MusicDTO withoutTitleMusicDTO() {
    MusicDTO musicDTO = new MusicDTO();

    musicDTO.setTitle(null);
    musicDTO.setDescription("Uma música icônica de John Lennon.");
    musicDTO.setReleaseDate(LocalDate.of(1971, 10, 11));
    musicDTO.setLyric(createValidLyricDTO());

    return musicDTO;
  }

  public static MusicDTO withoutDescriptionMusicDTO() {
    MusicDTO musicDTO = new MusicDTO();

    musicDTO.setTitle("Imagine");
    musicDTO.setDescription(null);
    musicDTO.setReleaseDate(LocalDate.of(1971, 10, 11));
    musicDTO.setLyric(createValidLyricDTO());

    return musicDTO;
  }

  public static MusicDTO withoutReleaseDateMusicDTO() {
    MusicDTO musicDTO = new MusicDTO();

    musicDTO.setTitle("Imagine");
    musicDTO.setDescription("Uma música icônica de John Lennon.");
    musicDTO.setReleaseDate(null);
    musicDTO.setLyric(createValidLyricDTO());

    return musicDTO;
  }

  public static MusicDTO withoutLyricMusicDTO() {
    MusicDTO musicDTO = new MusicDTO();

    musicDTO.setTitle("Imagine");
    musicDTO.setDescription("Uma música icônica de John Lennon.");
    musicDTO.setReleaseDate(LocalDate.of(1971, 10, 11));
    musicDTO.setLyric(null);

    return musicDTO;
  }
}
