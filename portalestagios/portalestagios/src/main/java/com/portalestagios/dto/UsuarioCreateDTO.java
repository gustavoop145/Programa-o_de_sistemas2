package com.portalestagios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
    @NotBlank(message = "nome é obrigatório")
    @Size(max = 150)
    String nome,

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    @Size(max = 180)
    String email,

    @NotBlank(message = "senha é obrigatória")
    @Size(min = 6, max = 60, message = "senha deve ter entre 6 e 60 caracteres")
    String senha
) {}