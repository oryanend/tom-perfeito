package com.oryanend.tom_perfeito_api.projections;

import java.util.UUID;

public interface UserMinProjection {
  UUID getId();

  String getUsername();

  String getEmail();
}
