package com.edunext.app.dtos;

import java.util.UUID;

import org.springframework.lang.NonNull;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoCadastroDTO(
    @NotNull(message = "ID do aluno é obrigatório")
    @NonNull
    UUID alunoId,

    @DecimalMin(value = "0.0", message = "Assiduidade deve ser no mínimo 0")
    @DecimalMax(value = "2.5", message = "Assiduidade deve ser no máximo 2.5")
    double assiduidade,
    
    @DecimalMin(value = "0.0", message = "Participação deve ser no mínimo 0")
    @DecimalMax(value = "2.5", message = "Participação deve ser no máximo 2.5")
    double participacao,
    
    @DecimalMin(value = "0.0", message = "Responsabilidade deve ser no mínimo 0")
    @DecimalMax(value = "2.5", message = "Responsabilidade deve ser no máximo 2.5")
    double responsabilidade,
    
    @DecimalMin(value = "0.0", message = "Sociabilidade deve ser no mínimo 0")
    @DecimalMax(value = "2.5", message = "Sociabilidade deve ser no máximo 2.5")
    double sociabilidade,
    
    @Size(max = 1000, message = "Observação muito longa (máximo 1000 caracteres)")
    String observacao
) {

}
