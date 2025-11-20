package com.edunext.app.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.edunext.app.model.Simulado;

public record SimuladoParaFazerDTO(
    Long id,
    String titulo,
    String nomeProfessor,
    String nomeTurma,
    LocalDateTime dataCriacao,
    List<QuestaoAlunoDTO> questoes
) {
    public SimuladoParaFazerDTO(Simulado simulado){
        this(simulado.getId(), simulado.getTitulo(), simulado.getProfessor().getNome(), simulado.getTurma().getNome(), simulado.getDataCriacao(), simulado.getQuestoes().stream().map(QuestaoAlunoDTO::new).collect(Collectors.toList()));
    }
}
