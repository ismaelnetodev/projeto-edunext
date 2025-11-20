package com.edunext.app.dtos;

public record QuestaoResumoDTO(
    Long questaoId,
    String enunciadoResumo,
    int totalRespostas,
    int totalAcertos,
    double taxaAcertoPercent
) {

}
