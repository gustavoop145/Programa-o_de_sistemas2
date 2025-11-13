package com.portalestagios.service.impl;

import com.portalestagios.entity.Estudante;
import com.portalestagios.entity.Inscricao;
import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.enums.StatusInscricao;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.EstudanteRepository;
import com.portalestagios.repository.InscricaoRepository;
import com.portalestagios.repository.VagaRepository;
import com.portalestagios.service.InscricaoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscricaoServiceImpl implements InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final VagaRepository vagaRepository;
    private final EstudanteRepository estudanteRepository;

    public InscricaoServiceImpl(InscricaoRepository inscricaoRepository,
                                VagaRepository vagaRepository,
                                EstudanteRepository estudanteRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.vagaRepository = vagaRepository;
        this.estudanteRepository = estudanteRepository;
    }

    /**
     * ordem: vagaId, estudanteId, emailLogado  <-- isso é importante para bater com o controller
     */
    @Override
    @Transactional
    public Inscricao inscrever(Long vagaId, Long estudanteId, String emailLogado) {
        Estudante est = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado: " + estudanteId));
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + vagaId));

        // owner-check via email (estudante só pode se inscrever em seu próprio perfil)
        if (emailLogado == null || est.getEmail() == null || !est.getEmail().equalsIgnoreCase(emailLogado)) {
            throw new SecurityException("Operação proibida");
        }

        if (vaga.getStatus() == StatusVaga.ENCERRADA) {
            throw new IllegalStateException("Vaga encerrada");
        }

        // Checa inscrição existente. Se seu repo não tiver esta assinatura, troque para existsByVagaIdAndEstudanteId(...)
        boolean alreadyExists;
        try {
            alreadyExists = inscricaoRepository.existsByEstudanteAndVaga(est, vaga);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            // fallback: se seu repo tem método por ids (ajuste conforme seu repository)
            alreadyExists = inscricaoRepository.existsByVagaIdAndEstudanteId(vagaId, estudanteId);
        }

        if (alreadyExists) {
            throw new IllegalStateException("Já inscrito");
        }

        Inscricao i = Inscricao.builder()
                .estudante(est)
                .vaga(vaga)
                .dataInscricao(LocalDateTime.now())
                .status(StatusInscricao.ATIVA)
                .build();

        return inscricaoRepository.save(i);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inscricao> listarPorVaga(Long vagaId, String emailLogado, boolean isAdmin) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + vagaId));

        if (!isAdmin) {
            String emailEmpresa = vaga.getEmpresa() != null ? vaga.getEmpresa().getEmail() : null;
            if (emailLogado == null || emailEmpresa == null || !emailEmpresa.equalsIgnoreCase(emailLogado)) {
                throw new SecurityException("Operação proibida");
            }
        }
        return inscricaoRepository.findByVaga_Id(vagaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inscricao> listarMinhas(String emailLogado) {
        Estudante est = estudanteRepository.findByEmailIgnoreCase(emailLogado)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado: " + emailLogado));
        return inscricaoRepository.findByEstudante_Id(est.getId());
    }

    @Override
    @Transactional
    public void cancelar(Long id, String emailLogado, boolean isAdmin) {
        Inscricao insc = inscricaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscrição não encontrada: " + id));

        if (!isAdmin) {
            String emailEst = insc.getEstudante() != null ? insc.getEstudante().getEmail() : null;
            if (emailLogado == null || emailEst == null || !emailEst.equalsIgnoreCase(emailLogado)) {
                throw new SecurityException("Operação proibida");
            }
        }
        inscricaoRepository.delete(insc);
    }
}
