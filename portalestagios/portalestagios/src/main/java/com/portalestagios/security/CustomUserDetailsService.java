// src/main/java/com/portalestagios/security/CustomUserDetailsService.java
package com.portalestagios.security;

import com.portalestagios.entity.Usuario;
import com.portalestagios.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service // <-- apenas este @Service implementando UserDetailsService
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;
    public CustomUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // nosso "username" é o email
        Usuario u = repo.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return new User(
                u.getEmail(),
                u.getSenhaHash(),
                u.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );
    }
}