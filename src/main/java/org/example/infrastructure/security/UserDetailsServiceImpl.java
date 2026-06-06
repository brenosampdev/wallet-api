package org.example.infrastructure.security;

import org.example.domain.entities.UserEntity;
import org.example.infrastructure.persistence.UserJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserJpaRepository userRepository;

  public UserDetailsServiceImpl(UserJpaRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado"));
    return new AuthenticatedUser(user.getId(), user.getEmail(), user.getRole(), user.getPasswordHash());
  }
}
