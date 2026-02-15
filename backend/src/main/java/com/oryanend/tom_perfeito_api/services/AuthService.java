package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.entities.Comment;
import com.oryanend.tom_perfeito_api.entities.User;
import com.oryanend.tom_perfeito_api.services.exceptions.UnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  @Autowired private UserService userService;

  public void validateSelfOrAdmin(User owner) {

    User me = userService.authenticated();

    if (me.hasRole("ROLE_ADMIN")) return;

    if (!me.getId().equals(owner.getId())) {
      throw new UnauthorizedActionException("Access denied. Should be self or admin");
    }
  }

  public void validateCreatedCommentBySelfOrAdmin(Comment comment) {
    User me = userService.authenticated();

    if (me.hasRole("ROLE_ADMIN")) {
      return;
    }

    if (!comment.getAuthor().getId().equals(me.getId())) {
      throw new UnauthorizedActionException("Access denied. Should be self or admin");
    }
  }
}
