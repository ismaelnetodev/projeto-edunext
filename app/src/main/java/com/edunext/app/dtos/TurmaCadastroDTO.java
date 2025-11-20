package com.edunext.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TurmaCadastroDTO(
    @NotBlank(message = "Nome da turma é obrigatório")
    String nome, 
    @NotNull(message = "Ano letivo é obrigatório")
    int anoLetivo) {

}
