package com.edunext.app.dtos;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;


public record SimuladoGerarDTO(
    String titulo,
    @NonNull
    Long turmaId,

    @NonNull
    Long disciplinaId,
    int numeroQuestoes,
    LocalDateTime inicioDisponivel,
    LocalDateTime fimDisponivel
) {

}
