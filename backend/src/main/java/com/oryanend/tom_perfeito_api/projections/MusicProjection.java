package com.oryanend.tom_perfeito_api.projections;

import java.time.Instant;
import java.util.UUID;

public interface MusicProjection {
  UUID getId();

  String getTitle();

  String getDescription();

  Instant getCreatedAt();

  UserMinProjection getCreatedBy();
}
