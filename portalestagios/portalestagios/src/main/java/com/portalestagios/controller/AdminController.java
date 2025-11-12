package com.portalestagios.controller;

import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.EmpresaRepository;
import com.portalestagios.repository.EstudanteRepository;
import com.portalestagios.repository.VagaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Admin")
@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final EmpresaRepository empresaRepository;
    private final EstudanteRepository estudanteRepository;
    private final VagaRepository vagaRepository;

    public AdminController(EmpresaRepository empresaRepository,
                           EstudanteRepository estudanteRepository,
                           VagaRepository vagaRepository) {
        this.empresaRepository = empresaRepository;
        this.estudanteRepository = estudanteRepository;
        this.vagaRepository = vagaRepository;
    }

    @Operation(summary = "Métricas do painel administrativo")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        long empresas = empresaRepository.count();
        long estudantes = estudanteRepository.count();
        long vagasAbertas = vagaRepository.countByStatus(StatusVaga.ABERTA);
        long vagasEncerradas = vagaRepository.countByStatus(StatusVaga.ENCERRADA);

        List<Map<String, Object>> vagasPorArea = vagaRepository.countVagasPorArea()
                .stream()
                .map(v -> Map.of(
                        "area", v.getArea(),
                        "total", v.getQtd()
                ))
                .toList();

        return Map.of(
                "qtdEmpresas", empresas,
                "qtdEstudantes", estudantes,
                "qtdVagasAbertas", vagasAbertas,
                "qtdVagasEncerradas", vagasEncerradas,
                "vagasPorArea", vagasPorArea
        );
    }
}
