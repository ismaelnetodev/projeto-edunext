package com.edunext.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfessorCadastroDTO(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "Login é obrigatório") String login,
    @NotBlank(message = "Senha é obrigatória") @Size(min = 4, message = "Senha deve ter no mínimo 4 caracteres") String password) {
}
