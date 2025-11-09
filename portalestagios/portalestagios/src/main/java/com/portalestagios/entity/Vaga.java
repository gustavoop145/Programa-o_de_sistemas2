// src/main/java/com/portalestagios/entity/Vaga.java
package com.portalestagios.entity;

import com.portalestagios.entity.enums.Modalidade;
import com.portalestagios.entity.enums.StatusVaga;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "vagas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Vaga {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String titulo;
    @Column(nullable=false, columnDefinition = "text") private String descricao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "area_id")
    private AreaInteresse area;

    @Column(nullable=false) private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Modalidade modalidade;

    @Column(nullable=false)
    private Integer cargaHoraria; // horas/semana

    @Column(nullable=false, columnDefinition = "text")
    private String requisitos;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatusVaga status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @OneToMany(mappedBy = "vaga", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Inscricao> inscricoes = new HashSet<>();
}