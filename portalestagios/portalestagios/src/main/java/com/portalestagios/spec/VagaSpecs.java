package com.portalestagios.spec;

import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.enums.Modalidade;
import com.portalestagios.entity.enums.StatusVaga;
import org.springframework.data.jpa.domain.Specification;

public class VagaSpecs {

  public static Specification<Vaga> comStatus(StatusVaga status) {
    return (root, q, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
  }

  public static Specification<Vaga> comAreaId(Long areaId) {
    return (root, q, cb) -> areaId == null ? cb.conjunction() : cb.equal(root.get("area").get("id"), areaId);
  }

  public static Specification<Vaga> comModalidade(Modalidade m) {
    return (root, q, cb) -> m == null ? cb.conjunction() : cb.equal(root.get("modalidade"), m);
  }

  public static Specification<Vaga> comLocalizacaoContendo(String termo) {
    return (root, q, cb) -> (termo == null || termo.isBlank())
        ? cb.conjunction()
        : cb.like(cb.lower(root.get("localizacao")), "%" + termo.toLowerCase() + "%");
  }
}