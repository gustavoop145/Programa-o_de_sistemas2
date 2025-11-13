package com.portalestagios.dto.inscricao;

import java.time.LocalDateTime;

public class InscricaoResponseDTO {

    private Long id;
    private Long vagaId;
    private Long estudanteId;
    private String status;
    private String observacoes;
    private LocalDateTime dataInscricao;

    // Construtor vazio (necessário para frameworks)
    public InscricaoResponseDTO() {}

    // Construtor que o MapperUtils ou outras classes podem estar chamando
    public InscricaoResponseDTO(Long id, Long vagaId, Long estudanteId, String status, String observacoes, LocalDateTime dataInscricao) {
        this.id = id;
        this.vagaId = vagaId;
        this.estudanteId = estudanteId;
        this.status = status;
        this.observacoes = observacoes;
        this.dataInscricao = dataInscricao;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVagaId() { return vagaId; }
    public void setVagaId(Long vagaId) { this.vagaId = vagaId; }

    public Long getEstudanteId() { return estudanteId; }
    public void setEstudanteId(Long estudanteId) { this.estudanteId = estudanteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataInscricao() { return dataInscricao; }
    public void setDataInscricao(LocalDateTime dataInscricao) { this.dataInscricao = dataInscricao; }
}
