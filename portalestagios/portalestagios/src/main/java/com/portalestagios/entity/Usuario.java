package com.portalestagios.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(name = "uk_usuarios_email", columnNames = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String nome;

  @Column(nullable = false, length = 180, unique = true)
  private String email;

  @Column(nullable = false, length = 120)
  private String senhaHash;

  @ManyToMany
  @JoinTable(
      name = "usuario_area",
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "area_id")
  )
  @Builder.Default
  private Set<AreaInteresse> areas = new LinkedHashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
  @Column(name = "role")
  @Builder.Default
  private Set<String> roles = new LinkedHashSet<>(); // "ROLE_USER", "ROLE_ADMIN", etc.
}