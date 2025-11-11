package com.portalestagios.controller;

import com.portalestagios.entity.AreaInteresse;
import com.portalestagios.repository.AreaInteresseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas-interesse") // <-- evita conflito com /api/areas do AreaController
@RequiredArgsConstructor
public class AreaInteresseController {

    private final AreaInteresseRepository areaInteresseRepository;

    // Lista todas as áreas de interesse (sem conflito de rota)
    @GetMapping
    public List<AreaInteresse> listar() {
        return areaInteresseRepository.findAll();
    }

    // Lista áreas de interesse de um estudante
    @GetMapping("/do-estudante")
    public List<AreaInteresse> listarDoEstudante(@RequestParam Long estudanteId) {
        return areaInteresseRepository.findAllByEstudanteId(estudanteId);
    }
}