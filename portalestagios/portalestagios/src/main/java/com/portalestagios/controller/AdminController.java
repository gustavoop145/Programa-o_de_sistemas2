// src/main/java/com/portalestagios/controller/AdminController.java
package com.portalestagios.controller;

import com.portalestagios.repository.EmpresaRepository;
import com.portalestagios.repository.EstudanteRepository;
import com.portalestagios.repository.VagaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

        // Conte de vagas por status (usa os métodos do repository)
        long vagasAbertas = 0L;
        long vagasEncerradas = 0L;
        try {
            vagasAbertas = vagaRepository.countByStatus(com.portalestagios.entity.enums.StatusVaga.ABERTA);
            vagasEncerradas = vagaRepository.countByStatus(com.portalestagios.entity.enums.StatusVaga.ENCERRADA);
        } catch (Throwable t) {
            // se não existir o método countByStatus por alguma razão, falha graciosa: 0
            // (log se desejar)
        }

        // Vagas por área: seu repository retorna uma projection VagasPorAreaView
        List<Map<String, Object>> vagasPorArea = new ArrayList<>();
        try {
            var raw = vagaRepository.countVagasPorArea(); // List<VagasPorAreaView>
            vagasPorArea = raw.stream()
                    .map(v -> {
                        Map<String, Object> m = new HashMap<>();
                        // projection has getArea() and getQtd()
                        try {
                            // usando reflexão typed-safe via interface
                            String area = (String) v.getClass().getMethod("getArea").invoke(v);
                            Object qtdObj = v.getClass().getMethod("getQtd").invoke(v);
                            long qtd = qtdObj instanceof Number ? ((Number) qtdObj).longValue() : 0L;
                            m.put("area", area);
                            m.put("total", qtd);
                        } catch (Exception ex) {
                            // fallback se a projection mudar
                            m.put("area", "UNKNOWN");
                            m.put("total", 0);
                        }
                        return m;
                    })
                    .collect(Collectors.toList());
        } catch (Throwable ex) {
            // fallback vazio
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("qtdEmpresas", empresas);
        resp.put("qtdEstudantes", estudantes);
        resp.put("qtdVagasAbertas", vagasAbertas);
        resp.put("qtdVagasEncerradas", vagasEncerradas);
        resp.put("vagasPorArea", vagasPorArea);

        return resp;
    }
}