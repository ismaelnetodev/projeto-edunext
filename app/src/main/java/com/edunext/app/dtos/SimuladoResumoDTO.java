package com.edunext.app.dtos;

import java.util.List;

public record SimuladoResumoDTO(
    Long simuladoId,
    String titulo,
    int totalAlunos,
    int alunosQueResponderam,
    double mediaNotas,
    double maiorNota,
    double menorNota,
    double taxaParticipacaoPercent,
    List<QuestaoResumoDTO> acertosPorQuestao
) {

}
