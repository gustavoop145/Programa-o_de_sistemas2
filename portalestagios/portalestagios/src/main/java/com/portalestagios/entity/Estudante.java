// src/main/java/com/portalestagios/entity/Estudante.java
package com.portalestagios.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "estudantes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Estudante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String nome;
    @Column(nullable=false, unique = true) private String cpf;
    @Column(nullable=false) private String curso;
    @Column(nullable=false, unique = true) private String email;
    @Column(nullable=false) private String telefone;

    @ManyToMany
    @JoinTable(
        name = "estudante_areas",
        joinColumns = @JoinColumn(name="estudante_id"),
        inverseJoinColumns = @JoinColumn(name="area_id")
    )
    private Set<AreaInteresse> areasInteresse = new HashSet<>();
}