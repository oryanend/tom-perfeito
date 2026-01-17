package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    public void validateSelfOrAdmin(Long userId) {
        User me = userService.authenticated();
        if (me.hasRole("ROLE_ADMIN")) {
            return;
        }
        if (!me.getId().equals(userId)) {
            throw new RuntimeException("Access denied. Should be self or admin");
        }
    }
}
