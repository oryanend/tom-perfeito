package com.oryanend.tom_perfeito_api.repositories;

import com.oryanend.tom_perfeito_api.entities.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MusicRepository extends JpaRepository<Music, UUID> {
    Page<Music> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
