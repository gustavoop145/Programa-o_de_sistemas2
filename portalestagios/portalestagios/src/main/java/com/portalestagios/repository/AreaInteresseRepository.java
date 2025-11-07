package com.portalestagios.repository;

import com.portalestagios.entity.AreaInteresse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaInteresseRepository extends JpaRepository<AreaInteresse, Long> {
  boolean existsByNomeIgnoreCase(String nome);
}
