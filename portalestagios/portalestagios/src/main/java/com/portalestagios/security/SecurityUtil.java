package com.portalestagios.security;

import com.portalestagios.entity.Vaga;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
  public boolean isOwner(Vaga vaga, Authentication auth) {
    if (vaga == null || auth == null) return false;
    String emailLogado = auth.getName(); // subject = email
    return vaga.getEmpresa() != null
        && vaga.getEmpresa().getEmail() != null
        && vaga.getEmpresa().getEmail().equalsIgnoreCase(emailLogado);
  }
}