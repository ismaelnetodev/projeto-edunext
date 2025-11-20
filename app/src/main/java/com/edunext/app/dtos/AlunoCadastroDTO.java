package com.edunext.app.dtos;

import org.springframework.lang.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AlunoCadastroDTO(
    @NotBlank(message = "Nome é obrigatório") 
    String nome,
    @NotBlank(message = "Matrícula é obrigatória") 
    String matricula,
    
    String fotoUrl,
    @NotNull(message = "Turma é obrigatória")
    @Positive(message = "ID da turma inválido")
    @NonNull
    Long turmaId
) {

}
