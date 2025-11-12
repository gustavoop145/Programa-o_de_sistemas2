package com.portalestagios.spec;

import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.enums.StatusVaga; // use o mesmo enum que você usa no projeto
import org.springframework.data.jpa.domain.Specification;

public final class VagaSpecifications {
  private VagaSpecifications() {}

  public static Specification<Vaga> byAreaId(Long areaId) {
    return (root, query, cb) -> {
      if (areaId == null) return null;
      // se o campo for root.get("area").get("id"), mantenha assim
      return cb.equal(root.get("area").get("id"), areaId);
    };
  }

  public static Specification<Vaga> byModalidade(String modalidade) {
    return (root, query, cb) -> {
      if (modalidade == null || modalidade.isBlank()) return null;
      return cb.like(cb.lower(root.get("modalidade")), "%" + modalidade.toLowerCase() + "%");
    };
  }

  public static Specification<Vaga> byLocalizacao(String localizacao) {
    return (root, query, cb) -> {
      if (localizacao == null || localizacao.isBlank()) return null;
      return cb.like(cb.lower(root.get("localizacao")), "%" + localizacao.toLowerCase() + "%");
    };
  }

  public static Specification<Vaga> onlyAbertas(Boolean abertas) {
    return (root, query, cb) -> {
      if (abertas == null || !abertas) return null;
      return cb.equal(root.get("status"), StatusVaga.ABERTA);
    };
  }
}