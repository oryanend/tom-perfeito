package com.oryanend.tom_perfeito_api.repositories;

import com.oryanend.tom_perfeito_api.entities.Music;
import com.oryanend.tom_perfeito_api.projections.MusicProjection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, UUID> {
  Page<MusicProjection> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  Page<MusicProjection> findAllBy(Pageable pageable);
}
