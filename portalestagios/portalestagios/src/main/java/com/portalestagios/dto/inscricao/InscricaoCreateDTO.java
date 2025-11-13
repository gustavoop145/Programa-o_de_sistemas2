package com.portalestagios.dto.inscricao;

public class InscricaoCreateDTO {
    private Long vagaId;
    private String observacoes;

    public InscricaoCreateDTO() {}

    public InscricaoCreateDTO(Long vagaId, String observacoes) {
        this.vagaId = vagaId;
        this.observacoes = observacoes;
    }

    public Long getVagaId() {
        return vagaId;
    }

    public void setVagaId(Long vagaId) {
        this.vagaId = vagaId;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}