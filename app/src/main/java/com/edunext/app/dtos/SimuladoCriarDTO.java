package com.edunext.app.dtos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;

public record SimuladoCriarDTO(
    String titulo,
    @NonNull
    Long turmaId,
    @NonNull
    Long disciplinaId,
    @NonNull
    List<Long> questoesId,
    LocalDateTime inicioDisponivel,
    LocalDateTime fimDisponivel
) {

}
