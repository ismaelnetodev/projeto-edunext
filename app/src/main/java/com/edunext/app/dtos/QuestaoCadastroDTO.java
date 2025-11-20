package com.edunext.app.dtos;

import java.util.List;

import com.edunext.app.enums.TipoQuestao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestaoCadastroDTO(
    @NotBlank(message = "Enunciado é obrigatório")
    String enunciado,
    @NotBlank(message = "Disciplina é obrigatória")
    String disciplina,
    @NotNull(message = "Tipo de questão é obrigatório")
    TipoQuestao tipo,
    @NotNull(message = "Pontuação é obrigatória")
    double pontuacao,
    String imagemUrl,
    @NotNull(message = "Alternativas são obrigatórias")
    List<String> alternativas,
    @NotBlank(message = "Resposta correta é obrigatória")
    String respostaCorreta
) {

}
