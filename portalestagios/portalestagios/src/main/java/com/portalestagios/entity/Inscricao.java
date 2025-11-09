// src/main/java/com/portalestagios/entity/Inscricao.java
package com.portalestagios.entity;

import com.portalestagios.entity.enums.StatusInscricao;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscricoes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Inscricao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id")
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vaga_id")
    private Vaga vaga;

    @Column(nullable=false)
    private LocalDateTime dataInscricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatusInscricao status;
}