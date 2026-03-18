package com.oryanend.tom_perfeito_api.projections;

import java.util.UUID;

public interface MusicProjection {
  UUID getId();

  String getTitle();

  String getDescription();
}
