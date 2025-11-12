package com.portalestagios.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "estudantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String cpf;

    private String telefone;

    private String cidade;

    private String curso;

    // relação com áreas de interesse
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "estudante_areas",  // igual ao nome real da tabela
        joinColumns = @JoinColumn(name = "estudante_id"),
        inverseJoinColumns = @JoinColumn(name = "area_id")
    )
    private Set<AreaInteresse> areas = new HashSet<>();

    // construtor auxiliar
    public Estudante(Long id) {
        this.id = id;
    }

    // ===== Aliases para compatibilidade com código legado =====
    public Set<AreaInteresse> getAreasInteresse() {
        return areas;
    }

    public void setAreasInteresse(Set<AreaInteresse> areas) {
        this.areas = areas;
    }
}