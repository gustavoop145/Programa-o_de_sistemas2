package com.portalestagios.service;

import com.portalestagios.entity.Inscricao;

import java.util.List;

public interface InscricaoService {

    // Inscrever: ordem vagaId, estudanteId, emailLogado (corrigido para bater com o controller)
    Inscricao inscrever(Long vagaId, Long estudanteId, String emailLogado);

    // Sobrecarga por DTO se quiser usar no futuro
    // Inscricao inscrever(InscricaoCreateDTO dto, String emailLogado);

    // Lista inscrições do estudante (por email do usuário logado)
    List<Inscricao> listarMinhas(String emailLogado);

    // Lista inscrições por vaga — permite controle por email da empresa ou admin flag
    List<Inscricao> listarPorVaga(Long vagaId, String emailLogado, boolean isAdmin);

    // Cancela inscrição (verifica permissões)
    void cancelar(Long id, String emailLogado, boolean isAdmin);
}