package com.edunext.app.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.edunext.app.model.Simulado;

public record SimuladoResponseDTO(
    Long id,
    String titulo,
    String nomeProfessor,
    String nomeTurma,
    LocalDateTime dataCriacao,
    LocalDateTime dataInicioDisponivel,
    LocalDateTime dataFimDisponivel,
    List<QuestaoResponseDTO> questoes
) {

    public SimuladoResponseDTO(Simulado simulado){
        this(
            simulado.getId(),
            simulado.getTitulo(),
            simulado.getProfessor().getNome(),
            simulado.getTurma().getNome(),
            simulado.getDataCriacao(),
            simulado.getDataInicioDisponivel(),
            simulado.getDataFimDisponivel(),
            simulado.getQuestoes()
                .stream()
                .map(QuestaoResponseDTO::new)
                .collect(Collectors.toList())
        );
    }

}
