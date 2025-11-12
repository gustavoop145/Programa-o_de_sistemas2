package com.portalestagios.service;

import com.portalestagios.dto.MapperUtils;
import com.portalestagios.dto.vaga.VagaCreateDTO;
import com.portalestagios.dto.vaga.VagaResponseDTO;
import com.portalestagios.dto.vaga.VagaUpdateDTO;
import com.portalestagios.entity.AreaInteresse;
import com.portalestagios.entity.Empresa;
import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.Estudante;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.AreaInteresseRepository;
import com.portalestagios.repository.EmpresaRepository;
import com.portalestagios.repository.EstudanteRepository;
import com.portalestagios.repository.VagaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final EmpresaRepository empresaRepository;
    private final AreaInteresseRepository areaRepository;
    private final EstudanteRepository estudanteRepository;

    public VagaService(VagaRepository vagaRepository,
                       EmpresaRepository empresaRepository,
                       AreaInteresseRepository areaRepository,
                       EstudanteRepository estudanteRepository) {
        this.vagaRepository = vagaRepository;
        this.empresaRepository = empresaRepository;
        this.areaRepository = areaRepository;
        this.estudanteRepository = estudanteRepository;
    }

    @Transactional
    public VagaResponseDTO create(@Valid VagaCreateDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada: " + dto.empresaId()));
        AreaInteresse area = areaRepository.findById(dto.areaId())
                .orElseThrow(() -> new EntityNotFoundException("Área não encontrada: " + dto.areaId()));

        Vaga v = Vaga.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .area(area)
                .localizacao(dto.localizacao())
                .modalidade(dto.modalidade())
                .cargaHoraria(dto.cargaHoraria())
                .requisitos(dto.requisitos())
                .status(StatusVaga.ABERTA)
                .empresa(empresa)
                .build();

        v = vagaRepository.save(v);
        return MapperUtils.toDTO(v);
    }

    @Transactional(readOnly = true)
    public Page<VagaResponseDTO> list(Pageable pageable,
                                      Long areaId,
                                      String modalidade,
                                      String localizacao,
                                      Boolean abertas) {

        // Exemplo simples: usar Specification se quiser filtros avançados.
        Vaga probe = new Vaga();
        if (localizacao != null && !localizacao.isBlank()) {
            probe.setLocalizacao(localizacao);
        }
        if (abertas != null && abertas) {
            probe.setStatus(StatusVaga.ABERTA);
        }
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Page<Vaga> page;
        if (areaId != null) {
            if (Boolean.TRUE.equals(abertas)) {
                page = vagaRepository.findAll(
                        (root, query, cb) -> cb.and(
                                cb.equal(root.get("status"), StatusVaga.ABERTA),
                                cb.equal(root.get("area").get("id"), areaId)
                        ), pageable
                );
            } else {
                page = vagaRepository.findAll(
                        (root, query, cb) -> cb.equal(root.get("area").get("id"), areaId),
                        pageable
                );
            }
        } else {
            page = vagaRepository.findAll(Example.of(probe, matcher), pageable);
        }

        return page.map(MapperUtils::toDTO);
    }

    @Transactional(readOnly = true)
    public VagaResponseDTO getById(Long id) {
        Vaga v = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));
        return MapperUtils.toDTO(v);
    }

    @Transactional(readOnly = true)
    public List<VagaResponseDTO> recomendadas(Long estudanteId, int limit) {
        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado: " + estudanteId));

        List<Long> areaIds = estudante.getAreas() == null
                ? Collections.emptyList()
                : estudante.getAreas().stream().map(AreaInteresse::getId).toList();

        Pageable topN = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "id"));
        Page<Vaga> page;
        if (!areaIds.isEmpty()) {
            page = vagaRepository.findByStatusAndArea_IdIn(StatusVaga.ABERTA, areaIds, topN);
        } else {
            page = vagaRepository.findByStatus(StatusVaga.ABERTA, topN);
        }

        return page.getContent().stream().map(MapperUtils::toDTO).toList();
    }

    @Transactional
    public VagaResponseDTO update(Long id, @Valid VagaUpdateDTO dto, Long empresaIdDona) {
        Vaga v = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));

        if (v.getEmpresa() == null || !v.getEmpresa().getId().equals(empresaIdDona)) {
            throw new SecurityException("Você não pode editar esta vaga.");
        }

        v.setTitulo(dto.titulo());
        v.setDescricao(dto.descricao());
        v.setLocalizacao(dto.localizacao());
        v.setModalidade(dto.modalidade());
        v.setCargaHoraria(dto.cargaHoraria());
        v.setRequisitos(dto.requisitos());

        v = vagaRepository.save(v);
        return MapperUtils.toDTO(v);
    }

    @Transactional
    public void delete(Long id, Long empresaIdDona) {
        Vaga v = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));

        if (v.getEmpresa() == null || !v.getEmpresa().getId().equals(empresaIdDona)) {
            throw new SecurityException("Você não pode excluir esta vaga.");
        }

        vagaRepository.delete(v);
    }

    @Transactional
    public VagaResponseDTO encerrar(Long id, Long empresaIdDona) {
        Vaga v = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));

        if (v.getEmpresa() == null || !v.getEmpresa().getId().equals(empresaIdDona)) {
            throw new SecurityException("Você não pode encerrar esta vaga.");
        }

        v.setStatus(StatusVaga.ENCERRADA);
        v = vagaRepository.save(v);
        return MapperUtils.toDTO(v);
    }
}
