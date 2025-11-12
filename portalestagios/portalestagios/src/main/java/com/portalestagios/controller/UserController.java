package com.portalestagios.controller;

import com.portalestagios.entity.Empresa;
import com.portalestagios.entity.Estudante;
import com.portalestagios.repository.EmpresaRepository;
import com.portalestagios.repository.EstudanteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Users")
@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final EstudanteRepository estudanteRepository;
    private final EmpresaRepository empresaRepository;

    public UserController(EstudanteRepository estudanteRepository,
                          EmpresaRepository empresaRepository) {
        this.estudanteRepository = estudanteRepository;
        this.empresaRepository = empresaRepository;
    }

    @Operation(summary = "Dados do usuário autenticado (email, roles e IDs de perfil)")
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return Map.of("authenticated", false);
        }

        String email = auth.getName();
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Optional<Estudante> estOpt = estudanteRepository.findByEmailIgnoreCase(email);
        Optional<Empresa>   empOpt = empresaRepository.findByEmailIgnoreCase(email);

        String nome = estOpt.map(Estudante::getNome)
                .orElseGet(() -> empOpt.map(Empresa::getNome).orElse(email));

        return Map.of(
                "authenticated", true,
                "email", email,
                "nome", nome,
                "roles", roles,
                "estudanteId", estOpt.map(Estudante::getId).orElse(null),
                "empresaId",   empOpt.map(Empresa::getId).orElse(null)
        );
    }
}