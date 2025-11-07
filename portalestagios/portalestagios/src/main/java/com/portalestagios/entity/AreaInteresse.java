package com.portalestagios.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "areas_interesse")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AreaInteresse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 120)
  private String nome;
}
