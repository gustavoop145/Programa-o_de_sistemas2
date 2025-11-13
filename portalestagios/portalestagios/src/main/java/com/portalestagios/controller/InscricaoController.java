package com.portalestagios.controller;

import com.portalestagios.dto.inscricao.InscricaoResponseDTO;
import com.portalestagios.entity.Inscricao;
import com.portalestagios.service.InscricaoService;
import com.portalestagios.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoService inscricaoService;

    // Inscrever um estudante em uma vaga
    @PostMapping("/vagas/{vagaId}/estudantes/{estudanteId}")
    public ResponseEntity<InscricaoResponseDTO> inscrever(
            @PathVariable Long vagaId,
            @PathVariable Long estudanteId,
            @RequestParam("email") String emailLogado
    ) {
        Inscricao inscricao = inscricaoService.inscrever(vagaId, estudanteId, emailLogado);
        return ResponseEntity.ok(MapperUtils.toInscricaoResponseDTO(inscricao));
    }

    // Listar inscrições de um estudante logado
    @GetMapping("/minhas")
    public ResponseEntity<List<InscricaoResponseDTO>> listarMinhas(@RequestParam("email") String emailLogado) {
        List<Inscricao> inscricoes = inscricaoService.listarMinhas(emailLogado);
        List<InscricaoResponseDTO> dtos = MapperUtils.convertList(inscricoes, MapperUtils::toInscricaoResponseDTO);
        return ResponseEntity.ok(dtos);
    }

    // Listar inscrições de uma vaga
    @GetMapping("/vaga/{vagaId}")
    public ResponseEntity<List<InscricaoResponseDTO>> listarPorVaga(
            @PathVariable Long vagaId,
            @RequestParam("email") String emailLogado,
            @RequestParam(value = "isAdmin", defaultValue = "false") boolean isAdmin
    ) {
        List<Inscricao> inscricoes = inscricaoService.listarPorVaga(vagaId, emailLogado, isAdmin);
        List<InscricaoResponseDTO> dtos = MapperUtils.convertList(inscricoes, MapperUtils::toInscricaoResponseDTO);
        return ResponseEntity.ok(dtos);
    }

    // Cancelar inscrição
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestParam("email") String emailLogado,
            @RequestParam(value = "isAdmin", defaultValue = "false") boolean isAdmin
    ) {
        inscricaoService.cancelar(id, emailLogado, isAdmin);
        return ResponseEntity.noContent().build();
    }
}