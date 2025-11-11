package com.portalestagios.controller;

import com.portalestagios.entity.AreaInteresse;
import com.portalestagios.repository.AreaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/areas")
@PreAuthorize("hasRole('ADMIN')")
public class AreaController {

    public static class AreaDTO {
        public Long id;
        @NotBlank public String nome;
        public AreaDTO() {}
        public AreaDTO(Long id, String nome){ this.id = id; this.nome = nome; }
    }

    @Autowired
    private AreaRepository areaRepository; // JpaRepository<AreaInteresse, Long>

    @GetMapping
    public List<AreaDTO> list() {
        return areaRepository.findAll().stream()
                .map(a -> new AreaDTO(a.getId(), a.getNome()))
                .collect(Collectors.toList()); // Java 8
    }

    @PostMapping
    public AreaDTO create(@RequestBody @Valid AreaDTO dto) {
        AreaInteresse a = new AreaInteresse();
        a.setNome(dto.nome);
        a = areaRepository.save(a);
        return new AreaDTO(a.getId(), a.getNome());
    }

    @PutMapping("/{id}")
    public AreaDTO update(@PathVariable Long id, @RequestBody @Valid AreaDTO dto) {
        AreaInteresse a = areaRepository.findById(id).orElseThrow();
        a.setNome(dto.nome);
        a = areaRepository.save(a);
        return new AreaDTO(a.getId(), a.getNome());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        areaRepository.deleteById(id);
    }
}
