package com.oryanend.tom_perfeito_api.repositories;

import com.oryanend.tom_perfeito_api.entities.Comment;
import com.oryanend.tom_perfeito_api.entities.CommentLike;
import com.oryanend.tom_perfeito_api.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
  Optional<CommentLike> findByUserAndComment(User user, Comment comment);

  Boolean existsByUserAndComment(User user, Comment comment);
}
