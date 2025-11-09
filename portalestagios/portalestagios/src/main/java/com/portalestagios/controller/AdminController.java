// src/main/java/com/portalestagios/controller/AdminController.java
package com.portalestagios.controller;

import com.portalestagios.dto.admin.MetricsDTO;
import com.portalestagios.dto.admin.VagasPorAreaDTO;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.EmpresaRepository;
import com.portalestagios.repository.EstudanteRepository;
import com.portalestagios.repository.VagaRepository;
import com.portalestagios.repository.projection.VagasPorAreaProjection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final EmpresaRepository empresaRepository;
    private final EstudanteRepository estudanteRepository;
    private final VagaRepository vagaRepository;

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Métricas do dashboard")
    public MetricsDTO metrics() {
        long qtdEmpresas = empresaRepository.count();
        long qtdEstudantes = estudanteRepository.count();
        long qtdVagasAbertas = vagaRepository.countByStatus(StatusVaga.ABERTA);
        long qtdVagasEncerradas = vagaRepository.countByStatus(StatusVaga.ENCERRADA);

        List<VagasPorAreaDTO> vagasPorArea = vagaRepository.countVagasPorArea()
                .stream()
                .map(p -> new VagasPorAreaDTO(p.getArea(), p.getTotal()))
                .toList();

        return MetricsDTO.builder()
                .qtdEmpresas(qtdEmpresas)
                .qtdEstudantes(qtdEstudantes)
                .qtdVagasAbertas(qtdVagasAbertas)
                .qtdVagasEncerradas(qtdVagasEncerradas)
                .vagasPorArea(vagasPorArea)
                .build();
    }
}