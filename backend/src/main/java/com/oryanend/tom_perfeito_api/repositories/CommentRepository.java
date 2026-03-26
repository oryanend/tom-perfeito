package com.oryanend.tom_perfeito_api.repositories;

import com.oryanend.tom_perfeito_api.entities.Comment;
import com.oryanend.tom_perfeito_api.entities.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByMusic(Music music, Pageable pageable);
}
