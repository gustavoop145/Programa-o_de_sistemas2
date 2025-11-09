package com.portalestagios.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "empresas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Empresa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String nome;
    @Column(nullable=false, unique = true) private String cnpj;
    @Column(nullable=false, unique = true) private String email;
    @Column(nullable=false) private String telefone;
    @Column(nullable=false) private String endereco;

    // Ex.: "Tecnologia;Educação" (simples por enquanto)
    private String areasAtuacao;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Vaga> vagas = new HashSet<>();
}