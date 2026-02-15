package com.oryanend.tom_perfeito_api.services;

import com.oryanend.tom_perfeito_api.dto.MusicMinDTO;
import com.oryanend.tom_perfeito_api.dto.RoleDTO;
import com.oryanend.tom_perfeito_api.dto.UserDTO;
import com.oryanend.tom_perfeito_api.entities.Music;
import com.oryanend.tom_perfeito_api.entities.Role;
import com.oryanend.tom_perfeito_api.entities.User;
import com.oryanend.tom_perfeito_api.projections.UserDetailsProjection;
import com.oryanend.tom_perfeito_api.repositories.MusicRepository;
import com.oryanend.tom_perfeito_api.repositories.RoleRepository;
import com.oryanend.tom_perfeito_api.repositories.UserRepository;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceAlreadyExistsException;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceNotFoundException;
import com.oryanend.tom_perfeito_api.util.CustomUserUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
  @Autowired private UserRepository repository;

  @Autowired private CustomUserUtil customUserUtil;

  @Autowired private RoleRepository roleRepository;

  @Autowired private MusicRepository musicRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
    if (result.isEmpty()) {
      throw new UsernameNotFoundException("Email not found");
    }

    User user = new User();
    user.setEmail(result.getFirst().getUsername());
    user.setPassword(result.getFirst().getPassword());
    for (UserDetailsProjection projection : result) {
      user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
    }

    return user;
  }

  protected User authenticated() {
    try {
      String username = customUserUtil.getLoggedUsername();
      return repository.findByEmail(username).get();
    } catch (Exception e) {
      throw new UsernameNotFoundException("Invalid user");
    }
  }

  @Transactional(readOnly = true)
  public UserDTO getMe() {
    User entity = authenticated();
    return new UserDTO(entity);
  }

  @Transactional(readOnly = true)
  public UserDTO findById(String id) {
    User entity =
        repository
            .findById(UUID.fromString(id))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return new UserDTO(entity);
  }

  @Transactional
  public UserDTO insert(UserDTO dto) {
    if (repository.findByEmail(dto.getEmail()).isPresent()) {
      throw new ResourceAlreadyExistsException("Email already in use, try another one.");
    }

    if (repository.findByUsername(dto.getUsername()).isPresent()) {
      throw new ResourceAlreadyExistsException("Username already in use, try another one.");
    }

    User entity = new User();
    copyDtoToEntity(dto, entity);

    entity.getRoles().clear();

    if (dto.getRoles().isEmpty()) {
      Role role = roleRepository.findByAuthority("ROLE_CLIENT");
      entity.getRoles().add(role);
    } else {
      for (RoleDTO roleDTO : dto.getRoles()) {
        Role role = roleRepository.getReferenceById(roleDTO.getId());
        entity.getRoles().add(role);
      }
    }

    entity.setPassword(passwordEncoder.encode(dto.getPassword()));
    entity = repository.save(entity);
    return new UserDTO(entity);
  }

  private void copyDtoToEntity(UserDTO dto, User entity) {
    entity.setUsername(dto.getUsername());
    entity.setEmail(dto.getEmail());
    entity.setPassword(dto.getPassword());

    entity.getRoles().clear();
    for (RoleDTO roleDTO : dto.getRoles()) {
      Role role = roleRepository.getReferenceById(roleDTO.getId());
      entity.getRoles().add(role);
    }

    entity.getMusicList().clear();
    for (MusicMinDTO musicMinDTO : dto.getMusics()) {
      Music music = musicRepository.getReferenceById(musicMinDTO.getId());
      entity.getMusicList().add(music);
    }
  }
}
